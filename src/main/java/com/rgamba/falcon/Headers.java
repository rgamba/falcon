package com.rgamba.falcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {
    Map<String, Header> _headers;

    Headers() {
        _headers = new HashMap<>();
    }

    public void add(String name, String value) {
        _headers.put(name.toUpperCase(), new Header(name, value));
    }

    public void add(String stringHeader) {
        Header header = Header.parse(stringHeader);
        _headers.put(header.getName().toUpperCase(), header);
    }

    public void remove(String name) {
        _headers.remove(name);
    }

    public Header[] toArray() {
        List<Header> result = new ArrayList<>();
        for (Header h : _headers.values())
            result.add(h);
        return result.toArray(new Header[result.size()]);
    }

    public boolean contains(String name) {
        return _headers.containsKey(name.toUpperCase());
    }

    public Header get(String name) {
        if (!contains(name))
            return null;
        return _headers.get(name.toUpperCase());
    }
}
