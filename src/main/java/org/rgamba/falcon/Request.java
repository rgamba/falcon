package org.rgamba.falcon;

import java.io.InputStreamReader;
import java.net.SocketAddress;


/**
 * Request
 *
 * <p>This represents an HTTP Request message. It will be initiated
 * by the client, parsed and passed along to to the appropriate
 * listener so it can generate a valid Response.
 *
 * @see <a  href="https://tools.ietf.org/html/rfc2616#page-35">HTTP RFC request</a>
 */
public class Request implements HttpMessage {
  private final Type _type;
  private final Headers _headers;
  private final SocketAddress _remoteAddress;
  private final InputStreamReader _bodyReader;
  private final String _uri;

  private Request(Builder builder) {
    _type = builder.type;
    _headers = builder.headers;
    _remoteAddress = builder.remoteAddress;
    _bodyReader = builder.bodyReader;
    _uri = builder.uri;
  }

  /**
   * Copy Constructor
   */
  public Request(Request request) {
    _type = request.getType();
    _headers = request.getHeaders();
    _remoteAddress = request.getRemoteAddress();
    _bodyReader = request.getBodyReader();
    _uri = request.getUri();
  }

  @Override
  public Request copy() {
    return new Request(this);
  }

  public Type getType() {
    return _type;
  }

  public Headers getHeaders() {
    return new Headers(_headers);
  }

  public SocketAddress getRemoteAddress() {
    return _remoteAddress;
  }

  public InputStreamReader getBodyReader() {
    return _bodyReader;
  }

  public String getUri() {
    return _uri;
  }

  public String readAllBody() {
    Header contentLength = _headers.get("Content-Length");
    if (contentLength == null) {
      return "";
    }
    int length = Integer.valueOf(contentLength.getValue());
    char[] buffer = new char[length];
    try {
      _bodyReader.read(buffer, 0, length - 1);
      return String.valueOf(buffer);
    } catch (Exception e) {
      System.out.println(e);
    }
    return "";
  }

  /**
   * Valid HTTP Message Types
   */
  public enum Type {
    POST("POST"), GET("GET"), HEAD("HEAD"), PUT("PUT"), DELETE("DELETE"), OPTION("OPTION");

    private final String name;

    Type(String n) {
      name = n;
    }

    public String toString() {
      return name;
    }
  }

  /**
   * Request builder
   */
  public static class Builder {
    private Type type;
    private Headers headers;
    private SocketAddress remoteAddress;
    private InputStreamReader bodyReader;
    private String uri;

    public Builder setType(Type type) {
      this.type = type;
      this.headers = new Headers();
      return this;
    }

    public Builder setHeaders(Headers headers) {
      this.headers = new Headers(headers);
      return this;
    }

    public Builder setHeader(String headerString) {
      this.headers.set(headerString);
      return this;
    }

    public Builder setRemoteAddress(SocketAddress ip) {
      this.remoteAddress = ip;
      return this;
    }

    public Builder setBodyReader(InputStreamReader reader) {
      this.bodyReader = reader;
      return this;
    }

    public Builder setUri(String uri) {
      this.uri = uri;
      return this;
    }

    public Request build() {
      return new Request(this);
    }
  }
}
