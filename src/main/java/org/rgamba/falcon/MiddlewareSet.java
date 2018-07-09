package org.rgamba.falcon;

import java.util.LinkedHashSet;
import java.util.Set;
import org.rgamba.falcon.middleware.AppendSlashMiddleware;


/**
 * MiddlewareSet
 *
 * <p>This class provides a series of convenience methods to easily
 * handle multiple middlewares and execute them when needed.
 */
public class MiddlewareSet {
  private final Set<Class<? extends Middleware>> _middleware = new LinkedHashSet<>();

  MiddlewareSet() {
    registerCommonMiddleware();
  }

  /**
   * Register a new Middleware class to be executed.
   * @param clazz The middleware class
   */
  public void register(Class<? extends Middleware> clazz) {
    _middleware.add(clazz);
  }

  /**
   * This method can be used to deregister a previously registered Middleware.
   * This can be usefull if you want to deregister a common middleware like {@link AppendSlashMiddleware}
   * @param clazz
   */
  public void deregister(Class<? extends Middleware> clazz) {
    _middleware.remove(clazz);
  }

  private void registerCommonMiddleware() {
    register(AppendSlashMiddleware.class);
  }

  /**
   * Execute all processRequest methods of all registered middleware
   * @param request The original Request object
   * @return The request object as returned by the last middleware
   */
  public Request processRequest(Request request) throws Exception {
    Request currRequest = request.copy();
    for (Class<? extends Middleware> clazz : _middleware) {
      Middleware middleware = clazz.newInstance();
      currRequest = middleware.processRequest(currRequest.copy());
    }
    return currRequest;
  }

  /**
   * Execute all processResponse methods of all registered middleware
   * @param request The original Request object or as returned by the last processRequest Middleware
   * @param response The original Response returned by the handler
   * @return The Response object as returned by the last middleware
   */
  public Response processResponse(Request request, Response response) throws Exception {
    Response currResp = response.copy();
    for (Class<? extends Middleware> clazz : _middleware) {
      Middleware middleware = clazz.newInstance();
      currResp = middleware.processResponse(request.copy(), currResp.copy());
    }
    return currResp.copy();
  }
}
