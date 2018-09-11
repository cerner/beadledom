package com.cerner.beadledom.client.proxy;

import com.cerner.beadledom.jaxrs.DelegatingGenericResponse;
import com.cerner.beadledom.jaxrs.GenericResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * A JAX-RS resource class proxy implementation with support for {@link GenericResponse}.
 *
 * @author John Leacox
 * @since 2.0
 */
class GenericClientProxy implements InvocationHandler {
  private final Object underlying;

  GenericClientProxy(Object underlying) {
    this.underlying = underlying;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Method underlyingMethod = underlying.getClass().getInterfaces()[0]
        .getMethod(method.getName(), method.getParameterTypes());

    Class<?> returnType = method.getReturnType();
    if (GenericResponse.class.isAssignableFrom(returnType)) {
      Response response;
      try {
        response = (Response) underlyingMethod.invoke(underlying, args);
      } catch (InvocationTargetException e) {
        throw e.getCause();
      }

      return buildGenericResponse(method, response);
    }

    try {
      return underlyingMethod.invoke(underlying, args);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }

  private Object buildGenericResponse(Method method, Response response) {
    int code = response.getStatus();
    Object entity = null;
    if (!(code < 200 || code >= 300) && response.hasEntity()) {
      ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
      Type bodyType = genericReturnType.getActualTypeArguments()[0];

      entity = response.readEntity(new GenericType(bodyType));
    }

    return DelegatingGenericResponse.create(entity, response);
  }
}
