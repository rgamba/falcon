package org.rgamba.falcon;

import java.io.*;
import java.net.Socket;
import java.util.function.Function;


public class HttpThread implements Runnable {
  private final Socket _client;
  private final OutputStreamWriter _out;
  private final InputStreamReader _in;
  private final Function<Request, Response> _handler;

  HttpThread(Socket client, Function<Request, Response> handler) throws IOException {
    _client = client;
    _out = new OutputStreamWriter(_client.getOutputStream());
    _in = new InputStreamReader(_client.getInputStream());
    _handler = handler;
  }

  @Override
  public void run() {
    Response response = null;
    try {
      Request request = new RequestParser(_client.getInputStream(), _client.getRemoteSocketAddress()).buildRequest();
      response = _handler.apply(request);
    } catch (IOException ex) {
      System.out.println(ex);
    } catch (Exception ex) {
      System.out.println(ex);
    } finally {
      sendResponse(response);
    }
  }

  private void sendResponse(Response response) {
    try {
      response.write(_out);
      _out.flush();
      _out.close();
    } catch (IOException ex) {
      System.out.println(ex);
    }
  }
}
