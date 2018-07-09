package org.rgamba.falcon;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.rgamba.falcon.errors.MethodNotImplemented;
import org.rgamba.falcon.errors.RequestError;
import org.rgamba.falcon.errors.ServerError;


/**
 * Http Request Router.
 *
 * <p>The router is an optional element that will take care of
 * handling and routing all requests based on the request method and
 * the path (uri). The client should first map each path to the
 * corresponding RequestHandler ({@code Class<? extends RequestHandler>})
 * That way, the router will know how to route each call.
 *
 * <p>It will also take care of gracefully handle exceptions and covert
 * them to response objects with the appropriate status code errors.
 */
public class Router {
  private Map<String, Class<? extends RequestHandler>> _routes = new HashMap<>();

  public void setHandler(String path, Class<? extends RequestHandler> handler) {
    _routes.put(path, handler);
  }

  public Response handle(Request request) {
    Class<? extends RequestHandler> handler = findHandler(request.getUri());
    if (handler == null) {
      return createResponseError(Response.Status.NOT_FOUND, "Not found");
    }
    Method methodToInvoke = getMethodToInvoke(handler, request.getType());
    if (methodToInvoke == null) {
      return createResponseError(Response.Status.METHOD_NOT_ALLOWED, "Not found");
    }
    try {
      RequestHandler handlerInstance = handler.newInstance();
      return invokeMethod(methodToInvoke, handlerInstance, request);
    } catch (Exception e) {
      return createResponseError(Response.Status.INTERNAL_ERROR, e.toString());
    }
  }

  private Response invokeMethod(Method methodToInvoke, RequestHandler handlerInstance, Request request)
      throws ReflectiveOperationException {
    try {
      return (Response) methodToInvoke.invoke(handlerInstance, request);
    } catch (MethodNotImplemented e) {
      return createResponseError(Response.Status.METHOD_NOT_ALLOWED, e.toString());
    } catch (ServerError e) {
      return createResponseError(Response.Status.INTERNAL_ERROR, e.toString());
    } catch (RequestError e) {
      return createResponseError(Response.Status.INTERNAL_ERROR, e.toString());
    }
  }

  private Response createResponseError(Response.Status status, String errorMessage) {
    return new Response.Builder().setStatusCode(status.getCode()).setBody(errorMessage).build();
  }

  private Method getMethodToInvoke(Class<? extends RequestHandler> handler, Request.Type type) {
    Class[] argTypes = new Class[]{Request.class};
    try {
      return handler.getDeclaredMethod(type.toString().toLowerCase(), argTypes);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  private Class<? extends RequestHandler> findHandler(String path) {
    // TODO: add regex match feature.
    for (Map.Entry<String, Class<? extends RequestHandler>> handler : _routes.entrySet()) {
      if (handler.getKey().equals(path)) {
        return handler.getValue();
      }
    }
    return null;
  }
}
