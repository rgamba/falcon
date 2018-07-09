package org.rgamba.falcon;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class RouterTest {
  private Router router;

  @BeforeTest
  public void setupRouter() {
    router = new Router();
    router.setHandler("/", HomeHandler.class);
    router.setHandler("/login", LoginHandler.class);
  }

  @Test
  public void testSetHandler() {
    Request loginReq = createRequest(Request.Type.GET, "/login");
    Response loginResp = router.handle(loginReq);
    Request homeReq = createRequest(Request.Type.GET, "/");
    Response homeResp = router.handle(homeReq);

    assertEquals(loginResp.getBody(), "this is a get request");
    assertEquals(homeResp.getBody(), "Homepage");
  }

  @Test
  public void testHandlerNotFound() {
    Request req = createRequest(Request.Type.GET, "/invalid");
    Response resp = router.handle(req);
    assertEquals(resp.getStatusCode(), 404);
  }

  @Test
  public void testInvalidMethod() {
    Request req = createRequest(Request.Type.POST, "/");
    Response resp = router.handle(req);
    assertEquals(resp.getStatusCode(), 405);
  }

  private Request createRequest(Request.Type type, String uri) {
    return new Request.Builder().setType(type).setUri(uri).build();
  }

  // Request handler classes below

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

  public static class HomeHandler implements RequestHandler {
    @Override
    public Response get(Request req) {
      return new Response.Builder().setBody("Homepage").build();
    }
  }
}
