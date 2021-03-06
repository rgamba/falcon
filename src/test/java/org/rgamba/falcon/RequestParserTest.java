package org.rgamba.falcon;

import org.rgamba.falcon.errors.BadRequest;
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
      + "Accept: */*" + CRLF;

  private String GET_REQ_WITH_QUERY_PARAMS = "GET /search?query=hello+world#stuff HTTP/1.1" + CRLF
      + "Host: mysite.com" + CRLF
      + "User-Agent: curl/7.49.1" + CRLF
      + "Accept: */*" + CRLF;

  private String GET_REQ_WITH_ABS_URI = "GET http://mysite.com:80/search?query=hello+world HTTP/1.1" + CRLF
      + "User-Agent: curl/7.49.1" + CRLF
      + "Accept: */*" + CRLF;

  private String GET_REQ_WITH_INVALID_URI = "GET invalid:asd!/search?query=hello+world HTTP/1.1" + CRLF
      + "User-Agent: curl/7.49.1" + CRLF
      + "Accept: */*" + CRLF;

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
    Request req = new RequestParser(is, null, 1024).buildRequest();
    assertEquals(req.getType(), Request.Type.POST);
    assertEquals(req.getUri(), "/login");
    assertEquals(req.getHeaders().toArray().length, 5);
    assertEquals(req.getHeaders().get("HOST").getValue(), "mysite.com");
  }

  @Test
  public void testQueryParams() throws Exception {
    InputStream is = new ByteArrayInputStream(GET_REQ_WITH_QUERY_PARAMS.getBytes());
    Request req = new RequestParser(is, null, 1024).buildRequest();
    assertEquals(req.getType(), Request.Type.GET);
    assertEquals(req.getUri(), "/search?query=hello+world#stuff");
    assertEquals(req.getPath(), "/search");
    assertEquals(req.getQueryParam("query").size(), 1);
    assertEquals(req.getQueryParam("query").get(0), "hello world");
  }

  @Test
  public void testAbsoluteUri() throws Exception {
    InputStream is = new ByteArrayInputStream(GET_REQ_WITH_ABS_URI.getBytes());
    Request req = new RequestParser(is, null, 1024).buildRequest();
    assertEquals(req.getType(), Request.Type.GET);
    assertEquals(req.getUri(), "http://mysite.com:80/search?query=hello+world");
    assertEquals(req.getPath(), "/search");
    assertEquals(req.getQueryParam("query").size(), 1);
    assertEquals(req.getQueryParam("query").get(0), "hello world");
  }

  @Test(expectedExceptions = BadRequest.class)
  public void invalidRequestType() throws Exception {
    InputStream is = new ByteArrayInputStream(INVALID_REQUEST.getBytes());
    new RequestParser(is, null, 1024).buildRequest();
  }

  @Test(expectedExceptions = BadRequest.class)
  public void testInvalidUri() throws Exception {
    InputStream is = new ByteArrayInputStream(GET_REQ_WITH_INVALID_URI.getBytes());
    new RequestParser(is, null, 1024).buildRequest();
  }

  @Test(expectedExceptions = BadRequest.class)
  public void invalidHttpVersion() throws Exception {
    InputStream is = new ByteArrayInputStream(INVALID_HTTP_TAG.getBytes());
    new RequestParser(is, null, 1024).buildRequest();
  }

  @Test(expectedExceptions = BadRequest.class)
  public void invalidPath() throws Exception {
    InputStream is = new ByteArrayInputStream(INVALID_PATH.getBytes());
    new RequestParser(is, null, 1024).buildRequest();
  }

  @Test(expectedExceptions = BadRequest.class)
  public void requestTooLarge() throws Exception {
    InputStream is = new ByteArrayInputStream(VALID_POST_REQUEST.getBytes());
    new RequestParser(is, null, 10).buildRequest();
  }
}
