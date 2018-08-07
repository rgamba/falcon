package org.rgamba.falcon;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class ResponseTest {
  @Test
  public void testResponseWrite() throws Exception {

    Response response = new Response.Builder().setStatusCode(Response.Status.OK)
        .setHeader("Content-Type", "text/plain")
        .setHeader("Date", "Tue, 03 Jul 2018 19:42:19 GMT")
        .setCookie(new Cookie("testcookie", "123456"))
        .setBody("Hello world!")
        .build();
    String expected = "HTTP/1.1 200 OK" + HttpConstants.CRLF
        + "Content-Length: 12" + HttpConstants.CRLF
        + "Content-Type: text/plain" + HttpConstants.CRLF
        + "Date: Tue, 03 Jul 2018 19:42:19 GMT" + HttpConstants.CRLF
        + "Server: falcon" + HttpConstants.CRLF
        + "Set-Cookie: testcookie=123456" + HttpConstants.CRLF
        + HttpConstants.CRLF
        + "Hello world!";

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(os);
    response.write(writer);
    writer.flush();
    writer.close();
    String realOutput = new String(os.toByteArray(), "UTF-8");
    assertEquals(realOutput, expected);
  }
}
