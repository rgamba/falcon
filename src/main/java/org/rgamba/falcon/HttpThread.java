package org.rgamba.falcon;

import java.io.*;
import java.net.Socket;
import java.util.function.Function;

import org.rgamba.falcon.errors.BadRequest;
import org.rgamba.falcon.errors.HttpError;


public class HttpThread implements Runnable {
  private final Socket _client;
  private final OutputStream _output;
  private final OutputStreamWriter _outputWriter;
  private final InputStream _input;
  private final InputStreamReader _inputReader;
  private final Function<Request, Response> _handler;
  private final MiddlewareSet _middlewareSet;
  private final long _maxRequestContentLength;

  HttpThread(Socket client, Function<Request, Response> handler, MiddlewareSet middlewareSet,
      long maxRequestContentLength) throws IOException {
    _client = client;
    _output = _client.getOutputStream();
    _input = _client.getInputStream();
    _outputWriter = new OutputStreamWriter(_output);
    _inputReader = new InputStreamReader(_input);
    _handler = handler;
    _middlewareSet = middlewareSet;
    _maxRequestContentLength = maxRequestContentLength;
  }

  @Override
  public void run() {
    Response response = null;
    try {
      Request request =
          new RequestParser(_input, _client.getRemoteSocketAddress(), _maxRequestContentLength).buildRequest();
      /* Apply request middleware */
      request = _middlewareSet.processRequest(request);
      /* Apply request handler */
      response = _handler.apply(request);
      /* Apply response middleware */
      response = _middlewareSet.processResponse(request, response);
    } catch (BadRequest e) {
      /* This should only be generated by RequestParser when a client sent a malformed request */
      response = createErrorResponse("", Response.Status.BAD_REQUEST);
    } catch (HttpError error) {
      /* Here we'll catch any exceptions raised by middleware */
      response = createErrorResponse(error);
    } catch (Exception ex) {
      /* Any other unexpected exceptions will be catched here */
      System.out.println("Client: " + _client);
      ex.printStackTrace();
      response = createErrorResponse(ex.getMessage(), Response.Status.INTERNAL_ERROR);
    } finally {
      sendResponse(response);
      closeClient();
    }
  }

  private void closeClient() {
    try {
      _input.close();
      _output.close();
      _client.close();
    } catch (IOException e) {
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
      response.write(_outputWriter);
      _outputWriter.flush();
      _outputWriter.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
