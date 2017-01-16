package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.proxy.ResourceProxyFactory;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * An implementation of {@link ResourceProxyFactory} for Resteasy.
 *
 * @author John Leacox
 * @since 2.0
 */
class ResteasyResourceProxyFactory implements ResourceProxyFactory {
  private final ResteasyWebTarget webTarget;

  ResteasyResourceProxyFactory(ResteasyWebTarget webTarget) {
    this.webTarget = webTarget;
  }

  @Override
  public <T> T proxy(Class<T> proxyInterface) {
    return webTarget.proxy(proxyInterface);
  }
}
