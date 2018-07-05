package org.rgamba.falcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketAddress;


/**
 * Request Parser
 *
 * Parse the raw HTTP request message and create
 * a request object that's usable by the request handlers.
 */
public class RequestParser {
  private static final int MAX_HEADERS = 100;

  private final InputStream _inputStream;
  private final InputStreamReader _inputReader;
  private final SocketAddress _remoteAddress;

  /**
   * Constructor
   * @param inputStream As returned by the Socket client
   * @param remoteAddress The remote IP address
   */
  RequestParser(InputStream inputStream, SocketAddress remoteAddress) {
    _inputStream = inputStream;
    _inputReader = new InputStreamReader(_inputStream);
    _remoteAddress = remoteAddress;
  }

  /**
   * Parse the request and get a usable Request object
   * This method should only be called once per request
   * @return Request object ready to use
   * @throws Exception In case an invalid message was sent
   */
  public Request buildRequest() throws Exception {
    String[] requestLine = getRequestLine();
    Request.Builder reqBuilder = new Request.Builder().setType(getMessageType(requestLine[0]))
        .setUri(requestLine[1])
        .setRemoteAddress(_remoteAddress);
    String headerStr;
    int headerCount = 0;
    while ((headerStr = getNextLine()) != null || headerCount < MAX_HEADERS) {
      if (headerStr.trim().equals("")) {
        break;
      }
      reqBuilder.setHeader(headerStr);
      headerCount++;
    }
    reqBuilder.setBodyReader(_inputReader);
    return reqBuilder.build();
  }

  private String[] getRequestLine() throws IOException {
    String reqLine = getNextLine();
    if (reqLine == null) {
      // Allow for buggy clients who send empty
      // first line before the request line.
      reqLine = getNextLine();
    }
    if (reqLine == null) {
      throw new InternalError("Invalid request line: " + reqLine);
    }
    String[] parts = reqLine.split(" ");
    if (parts.length < 3) {
      throw new InternalError("Invalid request line: " + reqLine);
    }
    final String type = parts[0].trim().toUpperCase();
    validateRequestType(type);
    validateHttpVersion(parts[2].trim().toUpperCase());
    return new String[]{type, parts[1].trim()};
  }

  private void validateRequestType(String t) {
    for (Request.Type type : Request.Type.values()) {
      if (type.name().equals(t)) {
        return;
      }
    }
    throw new InternalError("Invalid request type: " + t);
  }

  private void validateHttpVersion(String version) {
    if (!HttpConstants.PROTOCOL_VERSIONS.contains(version)) {
      throw new InternalError("Invalid HTTP version: " + version);
    }
  }

  private String getNextLine() throws IOException {
    StringBuilder header = new StringBuilder();
    int prevChar = 0;
    int curChar;
    while ((curChar = _inputReader.read()) != -1) {
      if (curChar == HttpConstants.LF_CHAR && prevChar == HttpConstants.CR_CHAR) {
        return header.toString();
      }
      header.append((char) curChar);
      prevChar = curChar;
    }
    return null;
  }

  private Request.Type getMessageType(String messageType) {
    return Request.Type.valueOf(messageType);
  }
}
