package org.rgamba.falcon;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.security.InvalidParameterException;

import static org.testng.Assert.*;


public class HeaderTest {

  @DataProvider
  private Object[][] headerTestInput() {
    //@formatter:off
    return new Object[][]{
        // Header string                Expected name   Expected value
        {"Content-Type: text/plain",    "Content-Type", "text/plain"},
        {" Content-Type: text:plain ",  "Content-Type", "text:plain"},
    };
    //@formatter:on
  }

  @Test(dataProvider = "headerTestInput")
  public void testParseString(String headerStr, String expName, String expValue) {
    Header h = Header.parse(headerStr);
    assertEquals(h.getName(), expName);
    assertEquals(h.getValue(), expValue);
  }

  @DataProvider
  private Object[][] headerInvalidInput() {
    return new Object[][]{{""}, {"invalid input"}, {" "}};
  }

  @Test(dataProvider = "headerInvalidInput", expectedExceptions = InvalidParameterException.class)
  public void testParseStringInvalidInput(String input) {
    Header.parse(input);
  }

  @Test
  public void testToString() {
    Header h = new Header("Content-type", "text/html");
    assertEquals(h.toString(), "Content-type: text/html\r\n");
  }

  @Test
  public void testEquals() {
    Header h1 = new Header("Content-Type", "text/html");
    Header h2 = new Header("CONTENT-TYPE", "text/html");

    assertTrue(h1.equals(h2));
  }

  @Test
  public void testCompareTo() {
    Header h1 = new Header("Content-Type", "text/html");
    Header h2 = new Header("Server", "falcon");
    Header h3 = new Header("Date", "test");

    assertTrue(h1.compareTo(h1) == 0);
    assertTrue(h1.compareTo(h2) < 0);
    assertTrue(h2.compareTo(h3) > 0);
  }
}
