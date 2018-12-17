package echofunction;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.google.gson.Gson;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Echo {
    @FunctionName("Echo")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        Map<String, String> queryParameters = request.getQueryParameters();
        Optional<String> body = request.getBody();
        BodyPayload payload = null;
        if (body.isPresent()) {
            payload = new Gson().fromJson(body.get(), BodyPayload.class);
        }

        HttpStatus resultCode = null; 
        
        String resultString = queryParameters.get("result");
        if (resultString != null) {
            try 
            {
                Integer resultInt = Integer.parseInt(resultString);
                resultCode = HttpStatus.valueOf(resultInt);
            } catch (NumberFormatException ex) {}
        }
        if (resultCode == null && payload != null && payload.result != null) {
            resultCode = HttpStatus.valueOf(payload.result);
        }
        if (resultCode == null) {
            resultCode = HttpStatus.OK;
        }

        Object message = queryParameters.get("message");
        if (message == null && payload != null) {
            message = payload.message;
        }
        

        return request.createResponseBuilder(resultCode).body(message).build();

        // Parse query parameter
        // String query = request.getQueryParameters().get("name");
        // String name = request.getBody().orElse(query);

        // if (name == null) {
        //     return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        // } else {
        //     return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        // }
    }

    public static class BodyPayload {
        public Integer result;
        public String message;
    }
}
