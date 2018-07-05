package org.rgamba.falcon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public interface HttpMessage {
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
}
