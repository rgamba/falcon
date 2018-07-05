package org.rgamba.falcon.examples;

import org.rgamba.falcon.HttpServer;
import org.rgamba.falcon.Request;
import org.rgamba.falcon.Response;

public class SimpleServer {
  public static void main(String[] args) {
    System.out.println("Started");
    HttpServer server = new HttpServer();
    server.setHandler(SimpleServer::handleRequest);
    server.listen(8080);
    System.out.print("Finished");
  }

  public static Response handleRequest(Request req) {
    String resHtml = "<h1>Requested: " + req.getUri() + "</h1>" + "<p>Body:<br>" + req.readAllBody() + "</p>";
    return new Response.Builder().setBody(resHtml).setHeader("Content-Type", "text/html").build();
  }
}
