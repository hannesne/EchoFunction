package echofunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.*;

/**
 * Unit test for Function class.
 */
public class EchoTest {
    @Test
    public void returnsDefaultsInResultWhenNoParametersProvided() {

        final HttpRequestMessage<Optional<String>> request = createRequest();
        final ExecutionContext context = createContext();

        // Invoke
        final HttpResponseMessage response = new Echo().run(request, context);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(response.getBody());

    }

    @Test
    public void returnsQueryParametersInResult() {
        HttpStatus expectedCode = HttpStatus.UNPROCESSABLE_ENTITY;
        String expectedMessage = "test message";
        final Map<String, String> queryParams = new HashMap<>();

        queryParams.put("result", String.valueOf(expectedCode.value()));
        queryParams.put("message", expectedMessage);

        final HttpRequestMessage<Optional<String>> request = createRequest(queryParams);
        final ExecutionContext context = createContext();

        // Invoke
        final HttpResponseMessage response = new Echo().run(request, context);

        // Verify
        assertEquals(expectedCode, response.getStatus());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    public void returnsBodyInResult() {
        HttpStatus expectedCode = HttpStatus.TEMPORARY_REDIRECT;
        String expectedMessage = "test message";
        String body = createBody(expectedCode, expectedMessage);

        final HttpRequestMessage<Optional<String>> request = createRequest(body);
        final ExecutionContext context = createContext();

        // Invoke
        final HttpResponseMessage response = new Echo().run(request, context);

        // Verify
        assertEquals(expectedCode, response.getStatus());
        assertEquals(expectedMessage, response.getBody());
    }

    private HttpRequestMessage<Optional<String>> createRequest(String body) {
        return createRequest(new HashMap<>(), body);
    }

    private HttpRequestMessage<Optional<String>> createRequest(Map<String, String> queryParams) {
        return createRequest(queryParams, null);
    }

    private String createBody(HttpStatus expectedCode, String expectedMessage) {
        Echo.BodyPayload payload = new Echo.BodyPayload();
        payload.result = expectedCode.value();
        payload.message = expectedMessage;
        Gson gson = new Gson();
        return gson.toJson(payload);
    }

    private ExecutionContext createContext() {
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
        return context;
    }

	private HttpRequestMessage<Optional<String>> createRequest() {

        final Map<String, String> queryParams = new HashMap<>();
        
        return createRequest(queryParams, null);
		
    }

	private HttpRequestMessage<Optional<String>> createRequest(Map<String,String> queryParams, String body) {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> request = mock(HttpRequestMessage.class);
        
        doReturn(Optional.ofNullable(body)).when(request).getBody();
        doReturn(queryParams).when(request).getQueryParameters();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(request).createResponseBuilder(any(HttpStatus.class));
        return request;
    }

    /**
     * Unit test for HttpTriggerJava method.
     */
    @Test
    @Ignore
    public void testHttpTriggerJava() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Azure");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Echo().run(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
    }
}
