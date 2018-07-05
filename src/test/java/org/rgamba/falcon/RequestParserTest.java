package org.rgamba.falcon;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.testng.Assert.*;


public class RequestParserTest {
  private final String CRLF = String.valueOf((char) 13) + String.valueOf((char) 10);
  //@formatter:off
  private String VALID_POST_REQUEST = "POST /login HTTP/1.1" + CRLF
    + "Host: mysite.com" + CRLF
    + "User-Agent: curl/7.49.1" + CRLF
    + "Accept: */*" + CRLF
    + "Content-Length: 17" + CRLF
    + "Content-Type: application/x-www-form-urlencoded" + CRLF
    + CRLF
    + "name=Ricardo&lastname=Gamba";

  private String VALID_GET_REQUEST = "GET /home HTTP/1.1" + CRLF
    + "Host: mysite.com" + CRLF
    + "User-Agent: curl/7.49.1" + CRLF
    + "Accept: */*" + CRLF
    + "Content-Length: 17" + CRLF;

  private String INVALID_REQUEST = "INVALID / HTTP/1.1" + CRLF
    + "Host: google.com" + CRLF
    + "User-Agent: curl/7.49.1" + CRLF;

  private String INVALID_HTTP_TAG = "POST / INVALID" + CRLF
    + "Host: google.com" + CRLF
    + "User-Agent: curl/7.49.1" + CRLF;

  private String INVALID_PATH = "POST HTTP/1.1" + CRLF
    + "Host: google.com" + CRLF
    + "User-Agent: curl/7.49.1" + CRLF;
  //@formatter:on

  @Test
  public void testParseValidPost() throws Exception {
    InputStream is = new ByteArrayInputStream(VALID_POST_REQUEST.getBytes());
    Request req = new RequestParser(is, null).buildRequest();
    assertEquals(req.getType(), Request.Type.POST);
    assertEquals(req.getUri(), "/login");
    assertEquals(req.getHeaders().toArray().length, 5);
    assertEquals(req.getHeaders().get("HOST").getValue(), "mysite.com");
  }

  @Test(expectedExceptions = InternalError.class)
  public void invalidRequestType() throws Exception {
    InputStream is = new ByteArrayInputStream(INVALID_REQUEST.getBytes());
    Request req = new RequestParser(is, null).buildRequest();
  }

  @Test(expectedExceptions = InternalError.class)
  public void invalidHttpVersion() throws Exception {
    InputStream is = new ByteArrayInputStream(INVALID_HTTP_TAG.getBytes());
    Request req = new RequestParser(is, null).buildRequest();
  }

  @Test(expectedExceptions = InternalError.class)
  public void invalidPath() throws Exception {
    InputStream is = new ByteArrayInputStream(INVALID_PATH.getBytes());
    Request req = new RequestParser(is, null).buildRequest();
  }
}
