package org.rgamba.falcon;

import org.rgamba.falcon.errors.BadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Request Parser
 *
 * <p>Parse the raw HTTP request message and create
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
   *
   * <p>This method should only be called once per request
   *
   * @return Request object ready to use
   * @throws Exception In case an invalid message was sent
   */
  public Request buildRequest() throws Exception {
    /*
    First line:
    GET /index.html HTTP/1.1
    */
    String[] requestLine = getRequestLine();
    final String messageType = requestLine[0];
    final String uri = requestLine[1];
    Request.Builder reqBuilder = new Request.Builder().setType(getMessageType(messageType))
        .setUri(uri)
        .setPath(extractPathFromUri(uri))
        .setQueryParams(HttpUtils.uriQueryStringToMap(getUriQueryString(uri)))
        .setRemoteAddress(_remoteAddress);
    /* URL part */
    URI url = null;
    try {
      url = URI.create(uri);
    } catch (IllegalArgumentException e) {
      throw new BadRequest("invalid URL format");
    }
    /*
    Header lines in the format:
    Key: Value
    */
    String headerStr;
    int headerCount = 0;
    while ((headerStr = getNextLine()) != null && headerCount < MAX_HEADERS) {
      if (headerStr.trim().equals("")) {
        break;
      }
      Header newHeader = Header.parse(headerStr);
      if (!HttpUtils.isValidToken(newHeader.getName())) {
        throw new BadRequest("invalid request headers");
      }
      reqBuilder.setHeader(newHeader);
      headerCount++;
    }
    if (reqBuilder.headers.contains("Content-Length")) {
      reqBuilder.setContentLength(Long.parseLong(reqBuilder.headers.get("Content-Length").getValue()));
    }

    /*
    RFC 2616: Must treat
    GET /index.html HTTP/1.1
    Host: www.google.com
     and
    GET http://www.google.com/index.html HTTP/1.1
    Host: ignore
    the same. In the second case, any Host line is ignored.
    */
    if (url.getAuthority() == null && reqBuilder.headers.contains("Host")) {
      String newUri = reqBuilder.headers.get("Host").getValue();
      if (newUri.endsWith("/")) {
        newUri = newUri.substring(0, newUri.length() - 1);
      }
      newUri = newUri + uri;
      try {
        url = URI.create(newUri);
      } catch (IllegalArgumentException e) {
        throw new BadRequest("Invalid URL format");
      }
    }

    reqBuilder.setUrl(url);
    reqBuilder.setBodyReader(_inputReader);

    return reqBuilder.build();
  }

  private String extractPathFromUri(String uri) throws Exception {
    if (uri.equals("*") || uri.startsWith("/")) {
      return uri.split("\\?")[0];
    }
    try {
      URL url = new URL(uri);
      return url.getPath();
    } catch (MalformedURLException ex) {
      // TODO: How should we response in this case?
      throw new BadRequest("invalid request URI");
    }
  }

  private String[] getRequestLine() throws IOException {
    String reqLine = getNextLine();
    if (reqLine == null) {
      // Allow for buggy clients who send empty
      // first line before the request line.
      reqLine = getNextLine();
    }
    if (reqLine == null) {
      throw new BadRequest("invalid request line");
    }
    String[] parts = reqLine.split(" ");
    if (parts.length < 3) {
      throw new BadRequest("invalid request line");
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
    throw new BadRequest("invalid request type");
  }

  private void validateHttpVersion(String version) {
    if (!HttpConstants.PROTOCOL_VERSIONS.contains(version)) {
      throw new BadRequest("unsupported HTTP version");
    }
  }

  private String getNextLine() throws IOException {
    StringBuilder header = new StringBuilder();
    int prevChar = 0;
    int curChar;
    while ((curChar = _inputReader.read()) != -1) { // TODO: timeouts here!
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

  private String getUriQueryString(String uri) {
    String[] parts = uri.split("\\?", 2);
    if (parts.length <= 1) {
      return "";
    }
    return parts[1].split("#", 2)[0];
  }
}
