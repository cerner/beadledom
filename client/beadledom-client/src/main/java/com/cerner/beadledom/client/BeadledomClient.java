package com.cerner.beadledom.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

/**
 * An abstract implementation of the {@link Client} that adapts the {@link WebTarget} to
 * {@link BeadledomWebTarget}.
 *
 * @author Sundeep Paruvu
 * @since 2.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public abstract class BeadledomClient implements Client {

  protected BeadledomClient() {}

  /**
   * A boolean indicator to check if the Client is still alive.
   *
   * @return true if the client is closed otherwise false.
   */
  public abstract boolean isClosed();

  /**
   * Build a new Beadledom web resource target.
   *
   * @param uri web resource URI. May contain template parameters
   * @return {@link BeadledomWebTarget}
   */
  public abstract BeadledomWebTarget target(String uri);

  /**
   * Build a new Beadledom web resource target.
   *
   * @param uri web resource URI. May contain template parameters
   * @return {@link BeadledomWebTarget}
   */
  public abstract BeadledomWebTarget target(URI uri);

  /**
   * Build a new Beadledom web resource target.
   *
   * @param uriBuilder web resource URI. May contain template parameters
   * @return {@link BeadledomWebTarget}
   */
  public abstract BeadledomWebTarget target(UriBuilder uriBuilder);

  /**
   * Build a new Beadledom web resource target.
   *
   * @param link web resource URI. May contain template parameters
   * @return {@link BeadledomWebTarget}
   */
  public abstract BeadledomWebTarget target(Link link);
}
