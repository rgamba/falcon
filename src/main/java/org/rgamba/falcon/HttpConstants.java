package org.rgamba.falcon;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class HttpConstants {
  public static final Set PROTOCOL_VERSIONS = Collections.unmodifiableSet(new HashSet(Arrays.asList("HTTP/1.1")));
  // Http message specifics
  public static final char CR_CHAR = (char) 13;
  public static final char LF_CHAR = (char) 10;
  public static final char SP_CHAR = (char) 32;
  public static final char HT_CHAR = (char) 9;
  public static final char DOUBLE_QUOTE_CHAR = (char) 34;
  public static final String CR = String.valueOf(CR_CHAR);
  public static final String LF = String.valueOf(LF_CHAR);
  public static final String SP = String.valueOf(SP_CHAR);
  public static final String HT = String.valueOf(HT_CHAR);
  public static final String CRLF = CR + LF;
  public static final String DOUBLE_QUOTE = String.valueOf(DOUBLE_QUOTE_CHAR);
  public static final String ESCAPE = "\\";
  // Default headers
  public static final String DEFAULT_HEADER_SERVER_NAME = "falcon";
  public static final String DEFAULT_HEADER_CONTENT_TYPE = "text/json; charset=utf-8";
  public static final String DEFAULT_HTTP_VERSION = "1.1";


  private HttpConstants() {
  }
}
