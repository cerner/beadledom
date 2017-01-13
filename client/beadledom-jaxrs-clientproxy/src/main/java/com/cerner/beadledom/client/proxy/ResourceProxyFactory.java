package com.cerner.beadledom.client.proxy;

/**
 * A factory for creating a proxied implementation of a JAX-RS resource.
 *
 * @author John Leacox
 * @since 2.0
 */
public interface ResourceProxyFactory {
  /**
   * Creates a proxied instance of the given JAX-RS resource class.
   */
  <T> T proxy(Class<T> proxyInterface);
}
