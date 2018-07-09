package org.rgamba.falcon;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Response
 *
 * <p>HTTP Response object that will be sent out by the
 * handler and will write directly to the socket output stream writer.
 */
public class Response implements HttpMessage {
  private final Headers _headers;
  private final int _status_code;
  private final String _body;

  private static final Map<Integer, String> statusNames;

  static {
    statusNames = new HashMap<>();
    statusNames.put(100, "CONTINUE");
    statusNames.put(200, "OK");
    statusNames.put(400, "BAD REQUEST");
    statusNames.put(404, "NOT FOUND");
  }

  private Response(Builder builder) {
    _headers = new Headers();
    setDefaultHeaders();
    for (Header h : builder.headers.toArray()) {
      _headers.set(h);
    }
    _status_code = builder.status_code;
    _body = builder.body;
  }

  /**
   * Constructor copy
   */
  public Response(Response resp) {
    _headers = resp.getHeaders();
    _status_code = resp.getStatusCode();
    _body = resp.getBody();
  }

  @Override
  public Response copy() {
    return new Response(this);
  }

  private void setDefaultHeaders() {
    _headers.set("Server", HttpConstants.DEFAULT_HEADER_SERVER_NAME);
    _headers.set("Date", HttpMessage.formatDate(new Date()));
    _headers.set("Content-Type", HttpConstants.DEFAULT_HEADER_CONTENT_TYPE);
  }

  /**
   * Write the response to the OutputStreamWriter
   * The response format will be:
   * <p>
   * <status-line> + CRLF
   * *(<header> + CRLF )
   * CRLF
   * <body>
   */
  public void write(OutputStreamWriter writer) throws IOException {
    // Status line
    writer.write(getStatusLine());
    writer.write(HttpConstants.CRLF);
    // Headers
    for (Header header : _headers.toArray()) {
      writer.write(header.toString());
    }
    writer.write(HttpConstants.CRLF);
    // Body
    if (_body != null) {
      writer.write(_body);
    }
  }

  private String getStatusLine() {
    return String.format("HTTP/%s %d %s", HttpConstants.DEFAULT_HTTP_VERSION, _status_code,
        statusNames.getOrDefault(_status_code, ""));
  }

  public String getBody() {
    return _body;
  }

  public int getStatusCode() {
    return _status_code;
  }

  public Headers getHeaders() {
    return new Headers(_headers);
  }

  /**
   * Response builder.
   */
  public static class Builder {
    private Headers headers = new Headers();
    private int status_code = 200;
    private String body;

    public Builder setHeader(String name, String value) {
      headers.set(name, value);
      return this;
    }

    public Builder setStatusCode(int code) {
      status_code = code;
      return this;
    }

    public Builder setStatusCode(Status code) {
      status_code = code.getCode();
      return this;
    }

    public Builder setBody(String body) {
      this.body = body;
      return this;
    }

    public Response build() {
      setContentLength();
      return new Response(this);
    }

    private void setContentLength() {
      if (body != null) {
        headers.set("Content-Length", String.valueOf(body.length()));
      }
    }
  }

  public enum Status {
    CONTINUE(100),
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    NOT_MODIFIED(304),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_ERROR(500);

    private final int code;

    Status(int n) {
      code = n;
    }

    public String toString() {
      return String.valueOf(code);
    }

    public int getCode() {
      return code;
    }
  }
}
