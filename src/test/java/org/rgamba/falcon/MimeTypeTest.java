package org.rgamba.falcon;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class MimeTypeTest {
  @Test
  public void testParseFromString() throws Exception {
    String test1 = "application/json; charset=utf-8";
    Map<String, String> params = new HashMap<>();
    params.put("charset", "utf-8");
    MimeType expected = new MimeType("application/json", params);

    String test2 = "application/json; charset=\"value with ; special chars\"";
    Map<String, String> params2 = new HashMap<>();
    params2.put("charset", "value with ; special chars");
    MimeType expected2 = new MimeType("application/json", params2);

    String test3 = "application/json; charset=\"value with \\\";\\\" special chars\"";
    Map<String, String> params3 = new HashMap<>();
    params3.put("charset", "value with \";\" special chars");
    MimeType expected3 = new MimeType("application/json", params3);

    assertEquals(MimeType.fromString(test1), expected);
    assertEquals(MimeType.fromString(test2), expected2);
    assertEquals(MimeType.fromString(test3), expected3);
  }
}
