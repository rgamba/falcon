package org.rgamba.falcon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;


public class HttpServer {
  private Function<Request, Response> _handler;

  public void setHandler(Function<Request, Response> handler) {
    _handler = handler;
  }

  public void setHandler(Router router) {
    setHandler(router::handle);
  }

  public void listen(int port) {
    try (ServerSocket socket = new ServerSocket(port)) {
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
    System.out.println("Accepting connection from " + client.getRemoteSocketAddress().toString());
    Runnable httpThread = new HttpThread(client, _handler);
    Thread thread = new Thread(httpThread);
    thread.run();
  }
}
