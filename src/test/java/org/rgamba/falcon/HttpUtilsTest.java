package org.rgamba.falcon;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class HttpUtilsTest {
  @DataProvider
  private Object[][] testTokens() {
    //@formatter:off
    return new Object[][]{
            //
            {"Content-Type",          true},
            {"Lettersand12345",       true},
            {"curl/7.49.1",           true},
            {"With spaces or tabs\t", false},
            {"(test)",                false},
            {"Content?",              false},
            {"<html>testing",         false},
    };
    //@formatter:on
  }

  @Test(dataProvider = "testTokens")
  public void testTokens(String token, boolean valid) {
    assertEquals(HttpUtils.isValidToken(token), valid);
  }
}
