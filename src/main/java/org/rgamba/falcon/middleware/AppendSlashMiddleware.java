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
    if (request.getPath().endsWith("/")) {
      return request;
    }
    return new Request.Builder(request)
        .setPath(request.getPath() + "/")
        .build();
  }
}
