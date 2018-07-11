package org.rgamba.falcon.examples;

import org.rgamba.falcon.*;

import java.util.List;
import java.util.Map;

public class SimpleServer {
  public static void main(String[] args) {
    HttpServer server = new HttpServer();
    server.setHandler(req -> {
      StringBuilder res = new StringBuilder();
      res.append("<h1>Requested: " + req.getUri() + "</h1>");
      res.append("<p>Request path: " + req.getPath() + "</p>");
      res.append("<p>Query params: </p>");
      for (Map.Entry<String, List<String>> entry : req.getQueryParams().entrySet()) {
        res.append("<b>" + entry.getKey() + "</b>: " + entry.getValue().toString() + "<br>");
      }
      res.append("<p>Body:<br>" + req.readAllBody() + "</p>");
      return new Response.Builder().setBody(res.toString()).setHeader("Content-Type", "text/html").build();
    });
    server.listen();
  }
}
