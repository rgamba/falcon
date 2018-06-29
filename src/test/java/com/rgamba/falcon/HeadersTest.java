package com.rgamba.falcon;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class HeadersTest {
    @Test
    public void testAddHeader() {
        Header expected = new Header("Content-type", "text/html");
        Header expected2 = new Header("Server", "test-server");
        Headers headers = new Headers();
        headers.add("Content-type", "text/html");
        assertTrue(headers.get("Content-type").equals(expected));
        headers.add("Server: test-server");
        assertTrue(headers.get("Server").equals(expected2));
    }

    @Test
    public void testRemoveHeader() {
        Headers headers = new Headers();
        headers.add("Server", "test");
        headers.remove("Server");
        assertFalse(headers._headers.containsKey("Server"));
    }

    @Test
    public void testContainsHeader() {
        Headers headers = new Headers();
        headers.add("Server", "test");
        assertTrue(headers.contains("Server"));
        assertFalse(headers.contains("invalid"));
    }

    @Test
    public void testGetHeader() {
        Headers headers = new Headers();
        headers.add("Server", "test");
        headers.add("Date", "2018-01-01");
        Header expected = new Header("Server", "test");
        assertEquals(headers.get("Server"), expected);
    }

    @Test
    public void testToArray() {
        Headers headers = new Headers();
        headers.add("Server", "test");
        headers.add("Date", "2018-01-01");
        Header[] headerArray = headers.toArray();
        assertEquals(headerArray.length, headers._headers.size());
        for (Header h : headerArray) {
            if (!headers.contains(h.getName())) {
                fail("Expected " + h.getName() + " in header array");
            }
        }
    }
}
