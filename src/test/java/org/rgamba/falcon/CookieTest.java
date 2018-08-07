package org.rgamba.falcon;

import org.testng.annotations.Test;

import java.time.ZonedDateTime;

import static org.testng.Assert.*;

public class CookieTest {
  @Test
  public void testConstructValidHeader() {
    Cookie cookie = new Cookie.Builder()
            .setName("mynewcookie")
            .setValue("123456")
            .setDomain("mydomain.com")
            .setPath("/secure")
            .setExpires(ZonedDateTime.parse("2015-02-20T06:30:00Z"))
            .setMaxAge(1000)
            .build();
    String expected = "mynewcookie=123456;Domain=mydomain.com;Path=%x2Fsecure;Expires=Fri, 20 Feb 2015 06:30:00 +0000;Max-Age=1000";
    assertEquals(cookie.toString(), expected);
  }

  @Test
  public void testSimpleCookie() {
    Cookie cookie = new Cookie("mynewcookie", "123456");
    String expected = "mynewcookie=123456";
    assertEquals(cookie.toString(), expected);
  }
}
