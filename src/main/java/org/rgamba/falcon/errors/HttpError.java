package org.rgamba.falcon.errors;

public class HttpError extends RuntimeException {
  private final int _statusCode;
  private final String _responseBody;

  public HttpError(String error, int status_code, String responseBody) {
    super(error);
    _statusCode = status_code;
    _responseBody = responseBody;
  }

  public HttpError(String error) {
    super(error);
    _statusCode = 500;
    _responseBody = "";
  }

  public HttpError() {
    this("");
  }

  public int getStatusCode() {
    return _statusCode;
  }

  public String getResponseBody() {
    return _responseBody;
  }
}
