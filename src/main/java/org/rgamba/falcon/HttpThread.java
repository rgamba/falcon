package org.rgamba.falcon;

import java.io.*;
import java.net.Socket;
import java.util.function.Function;
import org.rgamba.falcon.errors.HttpError;


public class HttpThread implements Runnable {
  private final Socket _client;
  private final OutputStreamWriter _out;
  private final InputStreamReader _in;
  private final Function<Request, Response> _handler;
  private final MiddlewareSet _middlewareSet;

  HttpThread(Socket client, Function<Request, Response> handler, MiddlewareSet middlewareSet) throws IOException {
    _client = client;
    _out = new OutputStreamWriter(_client.getOutputStream());
    _in = new InputStreamReader(_client.getInputStream());
    _handler = handler;
    _middlewareSet = middlewareSet;
  }

  @Override
  public void run() {
    Response response = null;
    try {
      Request request = new RequestParser(_client.getInputStream(), _client.getRemoteSocketAddress()).buildRequest();
      /* Apply request middleware */
      request = _middlewareSet.processRequest(request);
      /* Apply request handler */
      response = _handler.apply(request);
      /* Apply response middleware */
      response = _middlewareSet.processResponse(request, response);
    } catch (HttpError error) {
      /* Here we'll catch any exceptions raised by middleware */
      response = createErrorResponse(error);
    } catch (Exception ex) {
      /* Any other unexpected exceptions will be catched here */
      response = createErrorResponse(ex.getMessage(), Response.Status.INTERNAL_ERROR);
    } finally {
      sendResponse(response);
    }
  }

  private Response createErrorResponse(String message, Response.Status status) {
    return new Response.Builder().setStatusCode(status).setBody(message).build();
  }

  private Response createErrorResponse(HttpError error) {
    return new Response.Builder().setStatusCode(error.getStatusCode()).setBody(error.getResponseBody()).build();
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
