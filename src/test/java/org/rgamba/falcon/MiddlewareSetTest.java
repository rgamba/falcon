package org.rgamba.falcon;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MiddlewareSetTest {
  @Test
  public void testProcessRequest() throws Exception {
    MiddlewareSet middlewareSet = new MiddlewareSet();
    middlewareSet.register(FirstMiddleware.class);
    Request originalRequest = createRequest();
    Request finalRequest = middlewareSet.processRequest(originalRequest.copy());

    assertEquals(finalRequest.getHeaders().get("request-middleware").getValue(), "first");
  }

  @Test
  public void testProcessResponse() throws Exception {
    MiddlewareSet middlewareSet = new MiddlewareSet();
    middlewareSet.register(FirstMiddleware.class);
    Request originalRequest = createRequest();
    Response originalResponse = createResponse();
    Response finalResponse = middlewareSet.processResponse(originalRequest, originalResponse.copy());

    assertEquals(finalResponse.getHeaders().get("response-middleware").getValue(), "first");
  }

  @Test
  public void testMultipleMiddleware() throws Exception {
    MiddlewareSet middlewareSet = new MiddlewareSet();
    middlewareSet.register(FirstMiddleware.class);
    middlewareSet.register(SecondMiddleware.class);
    Request originalRequest = createRequest();
    Request finalRequest = middlewareSet.processRequest(originalRequest.copy());

    assertEquals(finalRequest.getHeaders().get("request-middleware").getValue(), "second");
  }

  @Test
  public void testDeregisterMiddleware() throws Exception {
    MiddlewareSet middlewareSet = new MiddlewareSet();
    middlewareSet.register(FirstMiddleware.class);
    middlewareSet.register(SecondMiddleware.class);
    middlewareSet.deregister(SecondMiddleware.class);
    Request originalRequest = createRequest();
    Request finalRequest = middlewareSet.processRequest(originalRequest.copy());

    assertEquals(finalRequest.getHeaders().get("request-middleware").getValue(), "first");
  }

  private Request createRequest() {
    return new Request.Builder().setType(Request.Type.GET)
            .setUri("/test")
            .setHeader("Content-Type: text/html")
            .setHeader("Server: Test")
            .setRemoteAddress(null)
            .build();
  }

  private Response createResponse() {
    return new Response.Builder().setBody("Hello world").build();
  }

  public static class FirstMiddleware implements Middleware {
    @Override
    public Request processRequest(Request request) {
      return new Request.Builder(request).setHeader("request-middleware", "first").build();
    }

    @Override
    public Response processResponse(Request request, Response response) {
      return new Response.Builder(response).setHeader("response-middleware", "first").build();
    }
  }

  public static class SecondMiddleware implements Middleware {
    @Override
    public Request processRequest(Request request) {
      return new Request.Builder(request).setHeader("request-middleware", "second").build();
    }

    @Override
    public Response processResponse(Request request, Response response) {
      return new Response.Builder(response).setHeader("response-middleware", "second").build();
    }
  }
}
