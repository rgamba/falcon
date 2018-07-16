package org.rgamba.falcon;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.testng.Assert.*;


public class RequestTest {
  @Test
  public void testRequestBuilder() {
    Request req = createRequestBuilder().build();
    assertEquals(req.getHeaders().get("Content-Type"), new Header("Content-Type", "text/html"));
    assertTrue(req.getHeaders().contains("Server"));
    assertEquals(req.getType(), Request.Type.GET);
    assertEquals(req.getUri(), "/test");
  }

  @Test
  public void testQueryParams() {
    Request req = createRequestBuilder()
        .setQueryParam("name", "ricardo")
        .build();

    assertEquals(req.getQueryParam("name").get(0), "ricardo");
    assertEquals(req.getQueryParam("name").size(), 1);
  }

  @Test
  public void testQueryParamsMultiple() {
    Request req = createRequestBuilder()
        .setQueryParam("names", "ricardo")
        .setQueryParam("names", "rodrigo")
        .setQueryParam("lastname", "gamba")
        .build();

    assertEquals(req.getQueryParam("names").get(0), "ricardo");
    assertEquals(req.getQueryParam("names").get(1), "rodrigo");
    assertEquals(req.getQueryParam("names").size(), 2);
    assertEquals(req.getQueryParam("lastname").size(), 1);
  }

  @Test
  public void testReadAllBody() {
    Request.Builder reqBuilder = createRequestBuilder();
    InputStream is = new ByteArrayInputStream("123456789".getBytes());
    reqBuilder.setBodyReader(new InputStreamReader(is)).setContentLength((long) 9);
    Request req = reqBuilder.build();

    assertEquals(req.readAllBody(), "123456789");
  }

  @Test
  public void testReadAllBodyLongContent() {
    StringBuffer sb = new StringBuffer();
    sb.append("0");
    long i = 0;
    for (i = 0; i < 5000; i++) {
      sb.append('a');
    }
    sb.append('b');

    Request.Builder reqBuilder = createRequestBuilder();
    InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
    reqBuilder.setBodyReader(new InputStreamReader(is)).setContentLength((long) sb.length());
    Request req = reqBuilder.build();

    String actual = req.readAllBody();
    assertEquals(actual, sb.toString());
  }

  @Test
  public void testWrongContentLength() {
    Request.Builder reqBuilder = createRequestBuilder();
    InputStream is = new ByteArrayInputStream("123456789".getBytes());
    reqBuilder.setBodyReader(new InputStreamReader(is)).setContentLength((long) 10);
    Request req = reqBuilder.build();
    // readAllBody will have an empty byte at the end due to the content length mismatch.
    assertNotEquals(req.readAllBody(), "123456789");
  }

  private Request.Builder createRequestBuilder() {
    return new Request.Builder().setType(Request.Type.GET)
        .setUri("/test")
        .setHeader("Content-Type: text/html")
        .setHeader("Server: Test")
        .setRemoteAddress(null);
  }
}
