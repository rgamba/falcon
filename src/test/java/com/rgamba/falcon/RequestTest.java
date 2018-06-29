package com.rgamba.falcon;

import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class RequestTest {
  @Test
  public void testRequestBuilder() {
    Request req = new Request.Builder().setType(Request.Type.GET)
        .setUri("/test")
        .setHeader("Content-Type: text/html")
        .setHeader("Server: Test")
        .setRemoteAddress(null)
        .build();
    assertEquals(req.getHeaders().get("Content-Type"), new Header("Content-Type", "text/html"));
    assertTrue(req.getHeaders().contains("Server"));
    assertEquals(req.getType(), Request.Type.GET);
    assertEquals(req.getUri(), "/test");
  }
}
