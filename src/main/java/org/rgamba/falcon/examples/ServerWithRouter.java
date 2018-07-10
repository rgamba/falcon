package org.rgamba.falcon.examples;

import org.rgamba.falcon.*;
import org.rgamba.falcon.errors.RequestError;

public class ServerWithRouter {
  public static void main(String[] args) {
    System.out.println("Started");
    HttpServer server = new HttpServer();
    Router router = new Router();
    router.setHandler("/", HomeHandler.class);
    router.setHandler("/login", LoginHandler.class);
    server.setHandler(router);
    server.listen(8080);
    System.out.print("Finished");
  }

  public static class HomeHandler implements RequestHandler {
    @Override
    public Response get(Request request) throws RequestError {
      return new Response.Builder().setBody("Hello world!").build();
    }
  }

  public static class LoginHandler implements RequestHandler {
    @Override
    public Response get(Request request) throws RequestError {
      return new Response.Builder().setBody("Login page").build();
    }

    @Override
    public Response post(Request request) throws RequestError {
      return new Response.Builder().setBody("Page post result").build();
    }
  }
}
