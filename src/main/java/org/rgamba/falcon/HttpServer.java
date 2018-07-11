package org.rgamba.falcon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;


public class HttpServer {
  private final int _port;
  private final MiddlewareSet _middlewareSet;
  private Function<Request, Response> _handler;

  public HttpServer() {
    _port = 8080;
    _middlewareSet = new MiddlewareSet();
  }

  public HttpServer(int port, MiddlewareSet middlewareSet) {
    _port = port;
    _middlewareSet = middlewareSet;
  }

  public HttpServer(int port, MiddlewareSet middlewareSet, Function<Request, Response> handler) {
    _port = port;
    _middlewareSet = middlewareSet;
    _handler = handler;
  }

  public HttpServer(int port, MiddlewareSet middlewareSet, Router router) {
    this(port, middlewareSet, router::handle);
  }

  public void setHandler(Function<Request, Response> handler) {
    _handler = handler;
  }

  public void setHandler(Router router) {
    setHandler(router::handle);
  }

  public void listen() {
    try (ServerSocket socket = new ServerSocket(_port)) {
      while (true) {
        try (Socket client = socket.accept()) {
          createNewThread(client);
        } catch (Exception threadEx) {
          System.out.println("Thread ex: " + threadEx.getMessage());
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void createNewThread(Socket client) throws IOException {
    client.setSoTimeout(5000);
    Runnable httpThread = new HttpThread(client, _handler, _middlewareSet);
    Thread thread = new Thread(httpThread);
    thread.run();
  }
}
