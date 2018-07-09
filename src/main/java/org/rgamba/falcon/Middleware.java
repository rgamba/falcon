package org.rgamba.falcon;

/**
 * Middleware
 *
 * <p>All middleware classes must implement this interface
 */
public interface Middleware {
  /**
   * This method will get executed before the request is passed on
   * to the request handler, this way the middleware can modify the
   * request before it gets handled.
   *
   * @param request The original request or the request returned by the previous middleware
   * @return The request that should be passed on to the next middleware. Return the original object if no change is needed.
   */
  default Request processRequest(Request request) {
    return request.copy();
  }

  /**
   * This method will be executed after the request has been handled by the
   * request handler. Thus, at this point we already have a Response object that we can
   * modify if needed before it is sent back.
   *
   * @param request The original request object as returned by the last middleware
   * @param response Either the original Response object or the object passed by the last middleware
   * @return The new response object. Return the original object if no change is needed.
   */
  default Response processResponse(Request request, Response response) {
    return response.copy();
  }
}
