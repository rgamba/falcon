package org.rgamba.falcon;

import org.rgamba.falcon.errors.MethodNotImplemented;
import org.rgamba.falcon.errors.RequestError;


public interface RequestHandler {
  default Response get(Request request) throws RequestError {
    throw new MethodNotImplemented();
  }

  default Response post(Request request) throws RequestError {
    throw new MethodNotImplemented();
  }

  default Response put(Request request) throws RequestError {
    throw new MethodNotImplemented();
  }

  default Response delete(Request request) throws RequestError {
    throw new MethodNotImplemented();
  }

  default Response head(Request request) throws RequestError {
    throw new MethodNotImplemented();
  }
}
