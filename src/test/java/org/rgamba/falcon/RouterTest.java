package org.rgamba.falcon;

import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class RouterTest {
  @Test
  public void testSetHandler() {
    Router router = new Router();
    router.setHandler("/login", LoginHandler.class);
    Request req = createRequest(Request.Type.GET, "/login");
    Response resp = router.handle(req);
    assertEquals(resp.getBody(), "this is a get request");
  }

  private Request createRequest(Request.Type type, String uri) {
    return new Request.Builder().setType(type).setUri(uri).build();
  }

  public static class LoginHandler implements RequestHandler {
    @Override
    public Response get(Request req) {
      return new Response.Builder().setBody("this is a get request").build();
    }

    @Override
    public Response post(Request req) {
      return new Response.Builder().setBody("this is a post request").build();
    }
  }
}
