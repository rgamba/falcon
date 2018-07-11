package org.rgamba.falcon;

import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
  private final String _path;
  private final Map<String, List<String>> _queryParams;

  /**
   * New objects must be created using the {@link Builder}
   */
  private Request(Builder builder) {
    _type = builder.type;
    _headers = builder.headers;
    _remoteAddress = builder.remoteAddress;
    _bodyReader = builder.bodyReader;
    _uri = builder.uri;
    _queryParams = builder.queryParams;
    _path = builder.path;
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
    _queryParams = request.getQueryParams();
    _path = request.getPath();
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

  public String getPath() {
    return _path;
  }

  /**
   * Read all the request body and return it as a string.
   *
   * <p>Note that you can only read the request body once, so if you already read
   * the InputStreamReader this method might return empty string. Otherwise if you
   * call this method and then try to read the inputstreamreader directly you might
   * get empty body.
   *
   * @return string representation of the request body
   */
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
   * Get a copy of all the query params for the current request
   * @return a list of all query parameters or an empty list if no parameters are found
   */
  public Map<String, List<String>> getQueryParams() {
    Map<String, List<String>> result = new HashMap<>();
    if (_queryParams.size() <= 0) {
      return result;
    }
    for (Map.Entry<String, List<String>> entry : _queryParams.entrySet()) {
      String[] values = entry.getValue().toArray(new String[entry.getValue().size()]);
      result.put(entry.getKey(), new ArrayList<>(Arrays.asList(values)));
    }
    return result;
  }

  /**
   * Get a copy of a single query param
   *
   * @param key The name of the parameter
   * @return A list of string values or an empty list if no key was found
   */
  public List<String> getQueryParam(String key) {
    if (!_queryParams.containsKey(key)) {
      return new ArrayList<>();
    }
    List<String> element = _queryParams.get(key);
    return new ArrayList<>(Arrays.asList(element.toArray(new String[element.size()])));
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
   *
   * <p>All new Request creations must be done using this builder
   */
  public static class Builder {
    private Type type;
    private Headers headers;
    private SocketAddress remoteAddress;
    private InputStreamReader bodyReader;
    private String uri;
    private String path;
    private Map<String, List<String>> queryParams = new HashMap<>();

    public Builder() {
    }

    public Builder(Request request) {
      type = request.getType();
      headers = request.getHeaders();
      remoteAddress = request.getRemoteAddress();
      bodyReader = request.getBodyReader();
      uri = request.getUri();
      queryParams = request.getQueryParams();
      path = request.getPath();
    }

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

    public Builder setHeader(String name, String value) {
      this.headers.set(name, value);
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

    public Builder setPath(String path) {
      this.path = path;
      return this;
    }

    public Builder setQueryParam(String key, String value) {
      if (!queryParams.containsKey(key)) {
        List<String> list = new ArrayList<>();
        list.add(value);
        queryParams.put(key, list);
      } else {
        queryParams.get(key).add(value);
      }
      return this;
    }

    public Request build() {
      return new Request(this);
    }
  }
}
