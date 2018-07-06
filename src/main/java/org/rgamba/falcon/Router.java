package org.rgamba.falcon;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class Router {
  private Map<String, Class<? extends RequestHandler>> _routes = new HashMap<>();

  public void setHandler(String path, Class<? extends RequestHandler> handler) {
    _routes.put(path, handler);
  }

  public Response handle(Request request) {
    Class<? extends RequestHandler> handler = findHandler(request.getUri());
    if (handler == null) {
      // TODO: return a not implemented or not found error.
      return null;
    }
    Method methodToInvoke = getMethodToInvoke(handler, request.getType());
    try {
      RequestHandler handlerInstance = handler.newInstance();
      return (Response) methodToInvoke.invoke(handlerInstance, request);
    } catch (Exception e) {
      return null;
    }
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
