package org.rgamba.falcon.examples;

import org.rgamba.falcon.*;

public class SimpleServer {
  public static void main(String[] args) {
    HttpServer server = new HttpServer();
    server.setHandler(req -> {
      String resHtml = "<h1>Requested: " + req.getUri() + "</h1>" + "<p>Body:<br>" + req.readAllBody() + "</p>";
      return new Response.Builder().setBody(resHtml).setHeader("Content-Type", "text/html").build();
    });
    server.listen();
  }
}
