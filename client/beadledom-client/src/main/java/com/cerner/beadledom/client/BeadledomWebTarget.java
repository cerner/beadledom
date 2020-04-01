package com.cerner.beadledom.client;

import javax.ws.rs.client.WebTarget;

/**
 * An abstract implementation of {@link WebTarget} that adds the @{link #proxy(Class)} method to
 * proxy the client.
 *
 * @author Sundeep Paruvu
 * @since 2.0
 * @deprecated As of 3.6, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public abstract class BeadledomWebTarget implements WebTarget {
  protected BeadledomWebTarget() {
  }

  public abstract <T> T proxy(Class<T> proxyInterface);
}
