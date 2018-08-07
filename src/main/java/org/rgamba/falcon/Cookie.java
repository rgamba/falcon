package org.rgamba.falcon;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cookie
 *
 * <p>This object represents an HTTP cookie as defined
 * in <a href="https://tools.ietf.org/html/rfc6265#section-4.1">RFC 6265</a>
 *
 * <p>This should be used along with Response in order to set new cookies
 * by setting the Set-Cookie response header
 */
public final class Cookie {
  private final String _name;
  private final String _value;
  private final ZonedDateTime _expires;
  private final Integer _maxAge;
  private final String _domain;
  private final String _path;
  private final boolean _secure;
  private final boolean _httpOnly;

  /**
   * Constructor
   *
   * @param builder The builder object
   */
  Cookie(Builder builder) {
    _name = builder.name;
    _value = builder.value;
    _expires = builder.expires;
    _maxAge = builder.maxAge;
    _domain = builder.domain;
    _path = builder.path;
    _secure = builder.secure;
    _httpOnly = builder.httpOnly;
  }

  /**
   * Copy constructor
   *
   * @param cookie The cookie object to copy from
   */
  public Cookie(Cookie cookie) {
    _name = cookie.getName();
    _value = cookie.getValue();
    _expires = cookie.getExpires();
    _maxAge = cookie.getMaxAge();
    _domain = cookie.getDomain();
    _path = cookie.getPath();
    _secure = cookie.isSecure();
    _httpOnly = cookie.isHttpOnly();
  }

  public Cookie(String name, String value) {
    this(new Builder().setName(name).setValue(value));
  }

  public String getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

  public ZonedDateTime getExpires() {
    return _expires;
  }

  public Integer getMaxAge() {
    return _maxAge;
  }

  public String getDomain() {
    return _domain;
  }

  public String getPath() {
    return _path;
  }

  public boolean isSecure() {
    return _secure;
  }

  public boolean isHttpOnly() {
    return _httpOnly;
  }

  @Override
  public String toString() {
    List<String> parts = new ArrayList<>();
    parts.add(buildPair(_name, _value));

    if(_domain != null) {
      parts.add(buildPair("Domain", _domain));
    }
    if(_path != null) {
      parts.add(buildPair("Path", _path));
    }
    if(_expires != null) {
      parts.add(buildPair("Expires", HttpUtils.formatDate(_expires)));
    }
    if(_maxAge != null) {
      parts.add(buildPair("Max-Age", String.valueOf(_maxAge)));
    }
    if(_httpOnly) {
      parts.add("HttpOnly");
    }
    if(_secure) {
      parts.add("Secure");
    }

    return String.join(";", parts);
  }

  private String buildPair(String name, String value) {
    return name + "=" + value;
  }

  public static class Builder {
    String name;
    String value;
    ZonedDateTime expires;
    Integer maxAge;
    String domain;
    String path;
    boolean secure;
    boolean httpOnly;

    public Builder setName(String name) {
      if(!HttpUtils.isValidToken(name)) {
        throw new IllegalArgumentException("cookie name is not a valid token");
      }
      this.name = name;
      return this;
    }

    public Builder setValue(String value) {
      this.value = value;
      return this;
    }

    public Builder setExpires(ZonedDateTime expires) {
      this.expires = expires;
      return this;
    }

    public Builder setMaxAge(Integer maxAge) {
      this.maxAge = maxAge;
      return this;
    }

    public Builder setDomain(String domain) {
      if(!HttpUtils.isValidToken(domain)) {
        throw new IllegalArgumentException("cookie domain is not a valid token");
      }
      this.domain = domain;
      return this;
    }

    public Builder setPath(String path) {
      path = path.replace("/", "%x2F");
      if(!HttpUtils.isValidToken(path)) {
        throw new IllegalArgumentException("path is not a valid token");
      }
      this.path = path;
      return this;
    }

    public Builder setSecure(boolean secure) {
      this.secure = secure;
      return this;
    }

    public Builder setHttpOnly(boolean httpOnly) {
      this.httpOnly = httpOnly;
      return this;
    }

    public Cookie build() {
      return new Cookie(this);
    }
  }
}
