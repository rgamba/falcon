package org.rgamba.falcon.examples;

import org.rgamba.falcon.*;
import org.rgamba.falcon.errors.HttpError;

public class ServerWithRouter {
  public static void main(String[] args) {
    /* Prepare middleware */
    MiddlewareSet middleware = new MiddlewareSet();
    middleware.register(TestMiddleware.class);

    /* Prepare router */
    Router router = new Router();
    router.setHandler("/", HomeHandler.class);
    router.setHandler("/login/", LoginHandler.class);

    /* Start server */
    HttpServer server = new HttpServer(8080, middleware, router);
    server.listen();
  }

  /**
   * Home request handler
   */
  public static class HomeHandler implements RequestHandler {
    @Override
    public Response get(Request request) throws HttpError {
      return new Response.Builder().setBody("Hello world!").build();
    }
  }

  /**
   * Login request handler
   */
  public static class LoginHandler implements RequestHandler {
    @Override
    public Response get(Request request) throws HttpError {
      return new Response.Builder().setBody("Login page").build();
    }

    @Override
    public Response post(Request request) throws HttpError {
      return new Response.Builder().setBody("Page post result").build();
    }
  }

  /**
   * Test middleware
   */
  public static class TestMiddleware implements Middleware {
    @Override
    public Request processRequest(Request request) {
      System.out.println("Request middleware");
      return request;
    }
  }
}
