package org.rgamba.falcon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;


public class HttpServer {
  private static final int MAX_THREADS = 10;
  private static final long DEFAULT_MAX_REQUEST_CONTENT_LENGTH = 10240; // 10 MB
  private static final int DEFAULT_PORT = 8080;
  private final ExecutorService _threadPool = Executors.newCachedThreadPool();
  private final int _port;
  private final MiddlewareSet _middlewareSet;
  private long _maxRequestContentLength = DEFAULT_MAX_REQUEST_CONTENT_LENGTH;
  private Function<Request, Response> _handler;
  private ServerSocket _socketServer;

  public HttpServer() {
    this(DEFAULT_PORT, new MiddlewareSet());
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

  /**
   * Set the handler to call whenever a new request comes in.
   * @param handler The handler to call
   */
  public void setHandler(Function<Request, Response> handler) {
    _handler = handler;
  }

  /**
   * Same as setHandler but instead of using a plain functional interface as
   * a handler, we'll use a {@link Router}.
   * @param router The router instance.
   */
  public void setRouter(Router router) {
    setHandler(router::handle);
  }

  /**
   * Set the max request size in bytes.
   * @param sizeInBytes The request's content length must be less than this number.
   */
  public void setMaxRequestSize(long sizeInBytes) {
    _maxRequestContentLength = sizeInBytes;
  }

  /**
   * Listen to new HTTP connections.
   * This will block the thread. If this is an issue, be sure to create a new
   * Thread and execute this in that thread.
   */
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
    Runnable httpThread = new HttpThread(client, _handler, _middlewareSet, _maxRequestContentLength);
    _threadPool.execute(httpThread);
  }
}
