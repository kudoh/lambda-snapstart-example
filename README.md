# Lambda SnapStart with Micronaut and Serverless Framework

## Install

Install Serverless Framework and Micronaut to your computer.

```shell
# Serverless Framework
npm install -g serverless

# Micronaut CLI
sdk install micronaut

# Create app
mn create-function-app com.mamezou.lambda-snapstart --features=aws-lambda --build=gradle --lang=java
cd lambda-snapstart/
```

## Implementation

Add CRaC to use runtime hooks.

```groovy
dependencies {
    // ...

    // add crac runtime hooks
    implementation group: 'io.github.crac', name: 'org-crac', version: '0.1.3'
}
```

Implements `org.crac.Resource` to your handler and add anything code to CRaC runtime hooks.

```java
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
```

## Build

Build with all-in-one jar by gradle task.

```shell
./gradlew shadowJar
```

## Deploy to Lambda

see `serverless.yml` to enable Lambda SnapStart.

```shell
serverless deploy
```
