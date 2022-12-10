package com.mamezou;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;

import java.security.SecureRandom;
import java.util.Collections;
public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
        implements Resource { // Interface追加

    @Inject
    ObjectMapper objectMapper;
    private static final SecureRandom random = new SecureRandom();
    private static int checkpointId;
    private static int restoreId;

    // Resource登録
    public FunctionRequestHandler() {
        Core.getGlobalContext().register(this);
    }

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            System.out.printf("checkpointId:%d,restoreId:%d%n", checkpointId, restoreId);
            String json = objectMapper.writeValueAsString(Collections.singletonMap("message", "Hello World"));
            response.setStatusCode(200);
            response.setBody(json);
        } catch (JsonProcessingException e) {
            response.setStatusCode(500);
        }
        return response;
    }

    // CRaC Runtime Hooks追加
    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) {
        checkpointId = random.nextInt();
        System.out.printf("BEFORE CHECKPOINT:%d%n", checkpointId);
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) {
        restoreId = random.nextInt();
        System.out.printf("AFTER RESTORE:%d%n", restoreId);
    }
}
