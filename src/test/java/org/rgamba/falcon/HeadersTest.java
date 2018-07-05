package org.rgamba.falcon;

import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class HeadersTest {
  @Test
  public void testAddHeader() {
    Header expected = new Header("Content-type", "text/html");
    Header expected2 = new Header("Server", "test-server");
    Headers headers = new Headers();
    headers.set("Content-type", "text/html");
    assertTrue(headers.get("Content-type").equals(expected));
    headers.set("Server: test-server");
    assertTrue(headers.get("Server").equals(expected2));
  }

  @Test
  public void testRemoveHeader() {
    Headers headers = new Headers();
    headers.set("Server", "test");
    headers.remove("Server");
    assertFalse(headers._headers.containsKey("Server"));
  }

  @Test
  public void testContainsHeader() {
    Headers headers = new Headers();
    headers.set("Server", "test");
    assertTrue(headers.contains("Server"));
    assertFalse(headers.contains("invalid"));
  }

  @Test
  public void testGetHeader() {
    Headers headers = new Headers();
    headers.set("Server", "test");
    headers.set("Date", "2018-01-01");
    Header expected = new Header("Server", "test");
    assertEquals(headers.get("Server"), expected);
  }

  @Test
  public void testToArray() {
    Headers headers = new Headers();
    headers.set("Server", "test");
    headers.set("Date", "2018-01-01");
    headers.set("Content-Type", "test");
    Header[] headerArray = headers.toArray();
    assertEquals(headerArray.length, headers._headers.size());
    Header[] expected = new Header[]{
        new Header("Content-Type", "test"),
        new Header("Date", "2018-01-01"),
        new Header("Server", "test")
    };
    for (int i = 0; i < headerArray.length; i++) {
      if (headerArray[i].getName() != expected[i].getName())
        fail("Expected " + expected[i].getName() + " and got: " + headerArray[i].getName());
    }
  }
}
