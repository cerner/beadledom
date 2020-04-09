package com.cerner.beadledom.client.proxy;

/**
 * A factory for creating a proxied implementation of a JAX-RS resource.
 *
 * @author John Leacox
 * @since 2.0
 * @deprecated As of 3.6, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public interface ResourceProxyFactory {
  /**
   * Creates a proxied instance of the given JAX-RS resource class.
   */
  <T> T proxy(Class<T> proxyInterface);
}
