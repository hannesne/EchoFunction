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
        BodyPayload payload = loadPayload(request);
        HttpStatus resultCode = loadResultCode(queryParameters, payload);
        Object message = loadMessage(queryParameters, payload);
        

        return request.createResponseBuilder(resultCode).body(message).build();
    }

	private BodyPayload loadPayload(HttpRequestMessage<Optional<String>> request) {
		Optional<String> body = request.getBody();
        BodyPayload payload = null;
        if (body.isPresent()) {
            payload = new Gson().fromJson(body.get(), BodyPayload.class);
        }
        return payload;
    }

    private Object loadMessage(Map<String, String> queryParameters, BodyPayload payload) {
        Object message = queryParameters.get("message");
        if (message == null && payload != null) {
            message = payload.message;
        }
        return message;
    }

    private HttpStatus loadResultCode(Map<String, String> queryParameters, BodyPayload payload) {
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
        return resultCode;
    }

    public static class BodyPayload {
        public Integer result;
        public String message;
    }
}
