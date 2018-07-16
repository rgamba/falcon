package org.rgamba.falcon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;


public class HttpServer {
  private static final int MAX_THREADS = 10;
  private final ExecutorService _threadPool = Executors.newCachedThreadPool();
  private final int _port;
  private final MiddlewareSet _middlewareSet;
  private Function<Request, Response> _handler;
  private ServerSocket _socketServer;

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
    openSocketServer();
    while (true) {
      try {
        Socket client = _socketServer.accept();
        createNewThread(client);
      } catch (IOException e) {
        System.out.println("Thread ex: " + e.getMessage());
      }
    }
  }

  private void openSocketServer() {
    try {
      _socketServer = new ServerSocket(_port);
    } catch (IOException e) {
      System.out.println("Unable to open socket server");
    }
  }

  private void createNewThread(Socket client) throws IOException {
    System.out.println("Creating new thread for client: " + client);
    client.setSoTimeout(5000);
    Runnable httpThread = new HttpThread(client, _handler, _middlewareSet);
    _threadPool.execute(httpThread);
  }
}
