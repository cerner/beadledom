package com.cerner.beadledom.client.proxy;

import java.lang.reflect.Proxy;

/**
 * An implementation of {@link ResourceProxyFactory} that delegates to an underlying
 * {@link ResourceProxyFactory} and adds support for
 * {@link com.cerner.beadledom.jaxrs.GenericResponse}.
 *
 * @author John Leacox
 * @since 2.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public class GenericResponseResourceProxyFactory implements ResourceProxyFactory {
  private final GenericResponseResourceTransformer transformer =
      new GenericResponseResourceTransformer();

  private final ResourceProxyFactory delegateProxyFactory;

  public GenericResponseResourceProxyFactory(ResourceProxyFactory delegateProxyFactory) {
    this.delegateProxyFactory = delegateProxyFactory;
  }

  @Override
  public <T> T proxy(Class<T> proxyInterface) {
    try {
      Class<?> delegateProxyClass = transformer.transform(proxyInterface);
      Object delegateProxy = delegateProxyFactory.proxy(delegateProxyClass);

      return proxyDelegate(proxyInterface, delegateProxy);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T proxyDelegate(Class<T> proxyInterface, Object delegateProxy) {
    Class<?>[] interfaces = {proxyInterface};
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    GenericClientProxy genericClientProxy = new GenericClientProxy(delegateProxy);
    Object proxy = Proxy.newProxyInstance(loader, interfaces, genericClientProxy);

    return (T) proxy;
  }
}
