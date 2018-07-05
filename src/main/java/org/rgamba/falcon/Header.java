package org.rgamba.falcon;

import java.security.InvalidParameterException;


public class Header implements Comparable<Header> {
  private static final String NAME_SEPARATOR = ":";

  private final String _name;
  private final String _value;

  public static Header parse(String headerString) throws InvalidParameterException {
    String[] parts = headerString.split(NAME_SEPARATOR, 2);
    if (parts.length <= 1) {
      throw new InvalidParameterException("Invalid header format, expected a colon separator");
    }
    return new Header(parts[0].trim(), parts[1].trim());
  }

  Header(String name, String value) {
    _name = name;
    _value = value;
  }

  public String getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Header)) {
      return false;
    }
    final Header otherHeader = (Header) other;
    return otherHeader.getName().toUpperCase().equals(_name.toUpperCase()) && otherHeader.getValue().equals(_value);
  }

  @Override
  public int hashCode() {
    return _name.hashCode();
  }

  @Override
  public String toString() {
    return String.format("%s: %s", _name, _value) + HttpConstants.CRLF;
  }

  @Override
  public int compareTo(Header other) {
    if (this == other) return 0;
    return _name.toUpperCase().compareTo(other.getName().toUpperCase());
  }
}
