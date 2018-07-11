package org.rgamba.falcon;

import org.rgamba.falcon.errors.MethodNotImplemented;
import org.rgamba.falcon.errors.HttpError;


/**
 * RequestHandler
 *
 * <p>Every request handler class must implement this interface
 * and must override all the methods that the request should be able
 * to handle.
 *
 * <p>The name of the method determines the HTTP request type.
 */
public interface RequestHandler {
  default Response get(Request request) throws HttpError {
    throw new MethodNotImplemented();
  }

  default Response post(Request request) throws HttpError {
    throw new MethodNotImplemented();
  }

  default Response put(Request request) throws HttpError {
    throw new MethodNotImplemented();
  }

  default Response delete(Request request) throws HttpError {
    throw new MethodNotImplemented();
  }

  default Response head(Request request) throws HttpError {
    throw new MethodNotImplemented();
  }
}
