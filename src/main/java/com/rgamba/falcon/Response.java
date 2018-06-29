package com.rgamba.falcon;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Response
 *
 * HTTP Response object that will be sent out by the
 * handler and will write directly to the socket output stream writer.
 */
public class Response {
    private final String CRLF = String.valueOf((char)13) + String.valueOf((char)10);

    private Headers _headers;
    private int _status_code;
    private String _body;

    private static final Map<Integer, String> statusNames;
    static {
        statusNames = new HashMap<>();
        statusNames.put(100, "CONTINUE");
        statusNames.put(200, "OK");
        statusNames.put(400, "BAD REQUEST");
        statusNames.put(404, "NOT FOUND");
    }

    private Response(Builder builder) {
        _headers = builder.headers;
        _status_code = builder.status_code;
        _body = builder.body;
    }

    public void write(OutputStreamWriter writer) throws IOException {
        // Status line
        writer.write(String.format("HTTP/1.1 %d %s", _status_code, statusNames.getOrDefault(_status_code, "")));
        writer.write(CRLF);
        // Headers
        for (Header header : _headers.toArray())
            writer.write(header.toString());
        writer.write(CRLF);
        // Body
        if (_body != null)
            writer.write(_body);
    }

    /**
     * Response builder.
     */
    public static class Builder {
        private Headers headers = new Headers();
        private int status_code = 200;
        private String body;

        public Builder setHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public Builder setStatusCode(int code) {
            status_code = code;
            return this;
        }

        public Builder setStatusCode(Status code) {
            status_code = code.ordinal();
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Response build() {
            setContentLength();
            return new Response(this);
        }

        private void setContentLength() {
            if (body != null)
                headers.add("Content-Length", String.valueOf(body.length()));
        }

        Builder() {
            headers.add("Server", "falcon");
            headers.add("Date", "");
            headers.add("Content-Type", "text/plain");
        }
    }

    public enum Status {
        CONTINUE(100),
        OK(200),
        CREATED(201),
        ACCEPTED(202),
        NO_CONTENT(204),
        MOVED_PERMANENTLY(301),
        FOUND(302),
        NOT_MODIFIED(304),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        METHOD_NOT_ALLOWED(405),
        INTERNAL_ERROR(500);

        private final int code;

        Status(int n) {
            code = n;
        }

        public String toString() {
            return String.valueOf(code);
        }
    }
}
