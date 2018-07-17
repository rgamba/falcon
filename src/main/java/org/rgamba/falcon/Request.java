package org.rgamba.falcon;

import org.rgamba.falcon.errors.BadRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.net.URI;
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
  private final URI _url;
  private final String _path;
  private final String _host;
  private final Long _contentLength;
  private final Map<String, List<String>> _queryParams;
  private  Map<String, List<String>> _formData;

  private final static long MAX_BODY_SIZE = (10 << 20); // 10 MB

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
    _host = builder.host;
    _url = builder.url;
    _contentLength = builder.contentLength;
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
    _host = request.getHost();
    _url = request.getUrl();
    _contentLength = request.getContentLength();
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

  public String getHost() {
    return _host;
  }

  public URI getUrl() {
    return _url;
  }

  public Long getContentLength() {
    return _contentLength;
  }

  /**
   * Read all the request body and return it as a string.
   *
   * <p>Note that you can only read the request body once, so if you already read
   * the InputStreamReader this method might return empty string. Otherwise if you
   * call this method and then try to read the inputstreamreader directly you might
   * get empty body.
   *
   * <p>Do not use this method if expect a very large request body as it can consume
   * considerable amount of resources given it will try and fit all the body on a
   * String. You can first inspect {@link Request#getContentLength()} to see if the
   * request body is too large.
   *
   * @return string representation of the request body
   */
  public String readAllBody() {
    if (_contentLength == null) {
      return "";
    }
    StringBuffer body = new StringBuffer();
    long step = 1024;
    long ceil;
    long offset = 0;
    do {
      if (step > _contentLength - offset) {
        ceil = _contentLength - offset;
      } else {
        ceil = step;
      }
      char[] buffer = new char[(int) ceil];
      try {
        _bodyReader.read(buffer, 0, (int) ceil);
        body.append(buffer);
      } catch (IOException ex) {
        break;
      }
      offset += ceil;
    } while (offset < _contentLength && offset <= MAX_BODY_SIZE);
    return body.toString();
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

  public void close() {
    try {
      this._bodyReader.close();
    } catch (IOException e) {
    }
  }

  /**
   * Parse the Content-Type header and return
   * it as a new MimeType object that is easy to use.
   */
  public MimeType getContentType() {
    try {
      return MimeType.fromString(_headers.get("Content-Type").getName());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }


  public List<String> getFormData(String key) {
    if (_formData == null) {
      MimeType contentType = this.getContentType();
      switch (contentType.getMediaType()) {
        case "multipart/form-data":
          if (contentType.getParam("boundary") == "") {
            throw new BadRequest("expected multipart boundary");
          }
          parseMultipartFormData(contentType.getParam("boundary"));
          break;
        case "application/x-www-form-urlencoded":
          parseUrlEncodedBody();
          break;
      }
    }

    if (!_formData.containsKey(key)) {
      return new ArrayList<>();
    }
    return _formData.get(key);
  }

  private void parseUrlEncodedBody() {
    String body = readAllBody();
    _formData = HttpUtils.uriQueryStringToMap(body);
  }

  private void parseMultipartFormData(String boundary) {
  }

  /**
   * Request builder
   *
   * <p>All new Request creations must be done using this builder
   */
  public static class Builder {
    private Type type;
    Headers headers = new Headers();
    private SocketAddress remoteAddress;
    private InputStreamReader bodyReader;
    private String uri;
    private String path;
    private String host;
    private URI url;
    private Map<String, List<String>> queryParams = new HashMap<>();
    private Long contentLength;

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
      host = request.getHost();
      url = request.getUrl();
      contentLength = request.getContentLength();
    }

    public Builder setType(Type type) {
      this.type = type;
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

    public Builder setHeader(Header header) {
      setHeader(header.getName(), header.getValue());
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

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setUrl(URI url) {
      this.url = url;
      return this;
    }

    public Builder setPath(String path) {
      this.path = path;
      return this;
    }

    public Builder setContentLength(Long contentLength) {
      this.contentLength = contentLength;
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

    public Builder setQueryParams(Map<String, List<String>> queryParams) {
      for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
        String[] values = entry.getValue().toArray(new String[entry.getValue().size()]);
        this.queryParams.put(entry.getKey(), new ArrayList<>(Arrays.asList(values)));
      }
      return this;
    }

    public Request build() {
      return new Request(this);
    }

  }
}
