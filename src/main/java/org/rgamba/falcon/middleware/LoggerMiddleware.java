package org.rgamba.falcon.middleware;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.rgamba.falcon.Middleware;
import org.rgamba.falcon.Request;


/**
 * Logger Middleware
 *
 * <p>Simple middleware that adds basic loging for each new
 * incomming request.
 */
public class LoggerMiddleware implements Middleware {
  private final static Logger LOGGER = Logger.getLogger(LoggerMiddleware.class.getName());

  public LoggerMiddleware() {
    LOGGER.setLevel(Level.INFO);
  }

  @Override
  public Request processRequest(Request request) {
    LOGGER.info(request.getType() + " - " + request.getPath() + " IP: " + request.getRemoteAddress().toString());
    return request;
  }
}
