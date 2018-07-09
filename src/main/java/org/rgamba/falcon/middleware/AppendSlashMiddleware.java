package org.rgamba.falcon.middleware;

import org.rgamba.falcon.Middleware;
import org.rgamba.falcon.Request;


/**
 * Append Slash Common Middleware
 *
 * <p>Simple middleware that adds "/" at the end of the
 * URI when the uri doesn't ends with "/"
 */
public class AppendSlashMiddleware implements Middleware {
  @Override
  public Request processRequest(Request request) {
    if (request.getUri().endsWith("/")) {
      return request;
    }
    return new Request.Builder().setHeaders(request.getHeaders())
        .setBodyReader(request.getBodyReader())
        .setType(request.getType())
        .setUri(request.getUri() + "/")
        .setRemoteAddress(request.getRemoteAddress())
        .build();
  }
}
