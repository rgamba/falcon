package com.rgamba.falcon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Headers {
  Map<String, Header> _headers;

  Headers() {
    _headers = new HashMap<>();
  }

  public void set(String name, String value) {
    set(new Header(name, value));
  }

  public void set(String stringHeader) {
    set(Header.parse(stringHeader));
  }

  public void set(Header header) {
    _headers.put(header.getName().toUpperCase(), header);
  }

  public void remove(String name) {
    _headers.remove(name);
  }

  public Header[] toArray() {
    List<Header> result = new ArrayList<>();
    for (Header h : _headers.values()) {
      result.add(h);
    }
    Header[] res = result.toArray(new Header[result.size()]);
    Arrays.sort(res);
    return res;
  }

  public boolean contains(String name) {
    return _headers.containsKey(name.toUpperCase());
  }

  public Header get(String name) {
    if (!contains(name)) {
      return null;
    }
    return _headers.get(name.toUpperCase());
  }
}
