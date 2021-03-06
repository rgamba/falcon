package org.rgamba.falcon;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HttpUtils {
  private static boolean[] isToken = new boolean[127];
  static {
    isToken[(int)'!'] = true;
    isToken[(int)'#'] = true;
    isToken[(int)'$'] = true;
    isToken[(int)'%'] = true;
    isToken[(int)'&'] = true;
    isToken[(int)'\''] = true;
    isToken[(int)'*'] = true;
    isToken[(int)'+'] = true;
    isToken[(int)'-'] = true;
    isToken[(int)'.'] = true;
    isToken[(int)'0'] = true;
    isToken[(int)'1'] = true;
    isToken[(int)'2'] = true;
    isToken[(int)'3'] = true;
    isToken[(int)'4'] = true;
    isToken[(int)'5'] = true;
    isToken[(int)'6'] = true;
    isToken[(int)'7'] = true;
    isToken[(int)'8'] = true;
    isToken[(int)'9'] = true;
    isToken[(int)'A'] = true;
    isToken[(int)'B'] = true;
    isToken[(int)'C'] = true;
    isToken[(int)'D'] = true;
    isToken[(int)'E'] = true;
    isToken[(int)'F'] = true;
    isToken[(int)'G'] = true;
    isToken[(int)'H'] = true;
    isToken[(int)'I'] = true;
    isToken[(int)'J'] = true;
    isToken[(int)'K'] = true;
    isToken[(int)'L'] = true;
    isToken[(int)'M'] = true;
    isToken[(int)'N'] = true;
    isToken[(int)'O'] = true;
    isToken[(int)'P'] = true;
    isToken[(int)'Q'] = true;
    isToken[(int)'R'] = true;
    isToken[(int)'S'] = true;
    isToken[(int)'T'] = true;
    isToken[(int)'U'] = true;
    isToken[(int)'W'] = true;
    isToken[(int)'V'] = true;
    isToken[(int)'X'] = true;
    isToken[(int)'Y'] = true;
    isToken[(int)'Z'] = true;
    isToken[(int)'^'] = true;
    isToken[(int)'_'] = true;
    isToken[(int)'`'] = true;
    isToken[(int)'a'] = true;
    isToken[(int)'b'] = true;
    isToken[(int)'c'] = true;
    isToken[(int)'d'] = true;
    isToken[(int)'e'] = true;
    isToken[(int)'f'] = true;
    isToken[(int)'g'] = true;
    isToken[(int)'h'] = true;
    isToken[(int)'i'] = true;
    isToken[(int)'j'] = true;
    isToken[(int)'k'] = true;
    isToken[(int)'l'] = true;
    isToken[(int)'m'] = true;
    isToken[(int)'n'] = true;
    isToken[(int)'o'] = true;
    isToken[(int)'p'] = true;
    isToken[(int)'q'] = true;
    isToken[(int)'r'] = true;
    isToken[(int)'s'] = true;
    isToken[(int)'t'] = true;
    isToken[(int)'u'] = true;
    isToken[(int)'v'] = true;
    isToken[(int)'w'] = true;
    isToken[(int)'x'] = true;
    isToken[(int)'y'] = true;
    isToken[(int)'z'] = true;
    isToken[(int)'|'] = true;
    isToken[(int)'~'] = true;
  }

  public static boolean isValidToken(String text) {
    for (int i = 0; i < text.length(); i++) {
      int c = (int) text.charAt(i);
      if (c >= isToken.length || !isToken[c]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Format the given date to a string format allowed and expected
   * by HTTP, RFC 822. See:
   * https://tools.ietf.org/html/rfc2616#page-20
   *
   * @param date the Date we want to format
   * @return String the formatted string ready to use
   */
  static String formatDate(Date date) {
    return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US).format(date);
  }

  static String formatDate(ZonedDateTime date) {
    String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
    return date.format(DateTimeFormatter.ofPattern(pattern));
  }

  public static Map<String, List<String>> uriQueryStringToMap(String uriQueryString) {
    Map<String, List<String>> result = new HashMap<>();
    String[] params = uriQueryString.split("&");
    for (String param : params) {
      String[] parts = param.split("=", 2);
      final String paramName = urlDecode(parts[0]);
      final String paramValue = parts.length > 1 ? urlDecode(parts[1]) : "";
      if (!result.containsKey(paramName)) {
        result.put(paramName, new ArrayList<>());
      }
      result.get(paramName).add(paramValue);
    }
    return result;
  }

  private static String urlDecode(String part) {
    try {
      return URLDecoder.decode(part, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }
}
