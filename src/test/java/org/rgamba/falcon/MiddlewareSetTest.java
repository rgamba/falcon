package org.rgamba.falcon;

import org.testng.annotations.Test;


public class MiddlewareSetTest {
  @Test
  public void testExecute() {
    MiddlewareSet middleware = new MiddlewareSet();
    middleware.set(MiddlewareSet.Event.BEFORE_REQUEST, (HttpMessage request) -> {
      return null;
    });
  }
}
