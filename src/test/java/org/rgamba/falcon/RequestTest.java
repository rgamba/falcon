package org.rgamba.falcon;

import org.testng.annotations.Test;

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

  private Request.Builder createRequestBuilder() {
    return new Request.Builder().setType(Request.Type.GET)
        .setUri("/test")
        .setHeader("Content-Type: text/html")
        .setHeader("Server: Test")
        .setRemoteAddress(null);
  }
}
