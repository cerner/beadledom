package com.cerner.beadledom.client;

import java.net.URI;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

/**
 * An abstract builder for creating an instance of {@link javax.ws.rs.client.Client}.
 *
 * <p>The static methods will use SPI to look for an implementation of this builder on the
 * classpath.
 *
 * @author John Leacox
 * @author Sundeep Paruvu
 * @since 2.0
 * @deprecated As of 3.6, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public abstract class BeadledomClientBuilder extends ClientBuilder {
  /**
   * Allows custom implementations to extend the {@code BeadledomJaxrsClientBuilder} class.
   */
  protected BeadledomClientBuilder() {
  }

  /**
   * Return a new instance of {@code BeadledomClientBuilder} by looking for an implementation
   * on the classpath using SPI.
   *
   * <p>If the SPI finds more than one implementation of {@link BeadledomClientBuilder} in the
   * classpath then it always creates the ClientBuilder for the implementation that comes first on
   * classpath. So it is non-deterministic that which client implementation will be created at
   * runtime.
   *
   * <p>However, if a specific implementation is required then the consumer should use the
   * implementation specific Factory class method #create to create the builder.
   */
  public static BeadledomClientBuilder newBuilder() {
    ServiceLoader<BeadledomClientBuilder> clientBuilderLoader =
        ServiceLoader.load(BeadledomClientBuilder.class);

    Iterator<BeadledomClientBuilder> iterator = clientBuilderLoader.iterator();

    if (!iterator.hasNext()) {
      throw new IllegalStateException(
          "An implementation of BeadledomClientBuilder was not found");
    }

    return iterator.next();
  }

  /**
   * Return a new instance of {@code {@link BeadledomWebTarget}} with the given String path by using
   * the default builder implementation on the classpath found using SPI.
   */
  public BeadledomWebTarget buildTarget(String baseUrl) {
    BeadledomClient beadledomClient = build();
    return beadledomClient.target(baseUrl);
  }

  /**
   * Return a new instance of {@code {@link BeadledomWebTarget}} with the given URI path by using
   * the default builder implementation on the classpath found using SPI.
   */
  public BeadledomWebTarget buildTarget(URI baseUrl) {
    BeadledomClient beadledomClient = build();
    return beadledomClient.target(baseUrl);
  }

  /**
   * Return a new instance of {@code {@link BeadledomWebTarget}} with the given UriBuilder path by
   * using the default builder implementation on the classpath found using SPI.
   */
  public BeadledomWebTarget buildTarget(UriBuilder baseUrl) {
    BeadledomClient beadledomClient = build();
    return beadledomClient.target(baseUrl);
  }

  /**
   * Return a new instance of {@code {@link BeadledomWebTarget}} with the given Link path by using
   * the default builder implementation on the classpath found using SPI.
   */
  public BeadledomWebTarget buildTarget(Link baseUrl) {
    BeadledomClient beadledomClient = build();
    return beadledomClient.target(baseUrl);
  }

  /**
   * Return a new default instance of {@code Client} by using the default builder implementation
   * on the classpath found using SPI.
   */
  public static Client newClient() {
    return newBuilder().build();
  }

  /**
   * Return a new instance of {@code Client} with the given configuration by using the the builder
   * implementation on the classpath found using SPI.
   *
   * <p>If multiple builder implementations are found, the first will be used, which may be
   * non-deterministic.
   *
   * @throws IllegalStateException if a builder implementation is not found
   */
  public static Client newClient(final Configuration configuration) {
    return newBuilder().withConfig(configuration).build();
  }

  /**
   * Builds the BeadledomClient.
   */
  public abstract BeadledomClient build();

  /**
   * Set the name to be used by the correlationId filter.
   */
  public abstract BeadledomClientBuilder setCorrelationIdName(String correlationIdName);

  /**
   * Retrieves the ClientConfiguration used to build the BeadledomClient.
   */
  public abstract BeadledomClientConfiguration getBeadledomClientConfiguration();

  /**
   * Sets the default connection pool size to be used.
   *
   * @return this builder
   */
  public abstract BeadledomClientBuilder setConnectionPoolSize(int connectionPoolSize);

  /**
   * Sets the default max connection pool size per route to be used.
   *
   * @return this builder
   */
  public abstract BeadledomClientBuilder setMaxPooledPerRouteSize(int maxPooledPerRouteSize);

  /**
   * Sets the default socket timeout to be used.
   * @return this builder
   */
  public abstract BeadledomClientBuilder setSocketTimeout(int socketTimeout, TimeUnit timeUnit);

  /**
   * Sets the default connection timeout to be used.
   *
   * @return this builder
   */
  public abstract BeadledomClientBuilder setConnectionTimeout(
      int connectionTimeout, TimeUnit timeUnit);

  /**
   * Sets the default TTL to be used.
   *
   * @return this builder
   */
  public abstract BeadledomClientBuilder setTtl(int ttl, TimeUnit timeUnit);
}
