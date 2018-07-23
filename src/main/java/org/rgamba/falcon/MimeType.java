package org.rgamba.falcon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MimeType {
  private final String _mediaType;
  private final Map<String, String> _params;

  MimeType(String mediaType, Map<String, String> params) {
    _mediaType = mediaType;
    _params = Collections.unmodifiableMap(new HashMap<>(params));
  }

  /**
   * Create a new MimeType object by parsing a mime type string.
   *
   * <p>It parses the given string as a media type value with any
   * optional parameters, per per RFC 1521. Media types are the values
   * present on the Content-Type and Content-Disposition HTTP Headers.
   *
   * <p>This is an example of a valid mime type string:
   *
   * application/json; charset="utf-8"
   * +-----------------+--------+------+
   * media type        param    param
   *                   name     value
   *
   * @param str The mime type string to parse
   * @return the new MimeType object
   * @throws IllegalArgumentException if the string is a malformed mime type string
   */
  public static MimeType fromString(String str) throws IllegalArgumentException {
    String[] parts = str.split(";", 2);
    final String mediaType = parts[0].toLowerCase().trim();
    Map<String, String> params = new HashMap<>();
    if (parts.length > 1) {
      params = parseParams(parts[1]);
    }
    return new MimeType(mediaType, params);
  }

  private static Map<String, String> parseParams(String str) {
    if (!str.endsWith(";")) {
      str = str + ";";
    }
    Map<String, String> result = new HashMap<>();
    StringBuffer curName = new StringBuffer();
    StringBuffer curValue = new StringBuffer();
    boolean inName = true;
    boolean inStr = false;
    boolean escaped = false;

    for (int i = 0; i < str.length(); i++) {
      final char curChar = str.charAt(i);
      switch (curChar) {
        case '=':
          // Parameter name-value separator
          if (!inStr) {
            inName = false;
            continue;
          }
          break;
        case ';':
          // Parameter separator
          if (!inStr) {
            inName = true;
            /**
             * Allow for parameter names joined by "*"
             * https://tools.ietf.org/html/rfc2231
             */
            if (curName.indexOf("*") == -1) {
              if (result.containsKey(curName.toString())) {
                throw new IllegalArgumentException("duplicate mime parameter name");
              }
              result.put(curName.toString(), curValue.toString());
            } else {
              String baseName = curName.toString().split("\\*", 2)[0];
              if (result.containsKey(baseName)) {
                result.put(baseName, result.get(baseName) + curValue.toString());
              } else {
                result.put(curName.toString(), curValue.toString());
              }
            }

            curName = new StringBuffer();
            curValue = new StringBuffer();
            continue;
          }
          break;
        case '"':
          // Quoted string
          if (!escaped) {
            inStr = !inStr;
            continue;
          }
          break;
        case '\\':
          // Allow for escaped string char
          if (!inStr) {
            throw new IllegalArgumentException("Invalid mime string");
          }
          escaped = true;
          continue;
        case ' ':
          // Ignore empty spaces
          if (!inStr) {
            continue;
          }
          break;
        default:
          escaped = false;
      }
      if (inName) {
        curName.append(curChar);
      } else {
        curValue.append(curChar);
      }
    }

    return result;
  }

  public String getMediaType() {
    return _mediaType;
  }

  public boolean containsParam(String paramName) {
    return _params.containsKey(paramName);
  }

  public String getParam(String paramName) {
    if (!_params.containsKey(paramName)) {
      return "";
    }
    return _params.get(paramName);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof MimeType)) {
      return false;
    }
    MimeType otherMime = (MimeType) other;
    if (!otherMime.getMediaType().equals(this._mediaType)) {
      return false;
    }
    if (otherMime._params.size() != this._params.size()) {
      return false;
    }
    for (Map.Entry<String, String> entry : otherMime._params.entrySet()) {
      if (!this._params.containsKey(entry.getKey())) {
        return false;
      }
      if (!this._params.get(entry.getKey()).equals(entry.getValue())) {
        return false;
      }
    }
    return true;
  }
}
