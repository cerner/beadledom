package com.cerner.beadledom.client;

import com.google.auto.value.AutoValue;
import java.security.KeyStore;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * Resteasy client builder to use with guice.
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@AutoValue
@Deprecated
public abstract class BeadledomClientConfiguration {
  private static final int DEFAULT_CONNECTION_POOL_SIZE = 200;
  private static final int DEFAULT_MAX_POOLED_PER_ROUTE = 100;
  private static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 10000;
  private static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 10000;
  private static final int DEFAULT_TTL_MILLIS = 1800000;

  public Builder newBuilder() {
    return BeadledomClientConfiguration.builder(this);
  }

  public static Builder builder(BeadledomClientConfiguration config) {
    return config.toBuilder();
  }

  /**
   * Default client config builder.
   */
  public static Builder builder() {
    return new AutoValue_BeadledomClientConfiguration.Builder()
        .connectionPoolSize(DEFAULT_CONNECTION_POOL_SIZE)
        .maxPooledPerRouteSize(DEFAULT_MAX_POOLED_PER_ROUTE)
        .socketTimeoutMillis(DEFAULT_SOCKET_TIMEOUT_MILLIS)
        .connectionTimeoutMillis(DEFAULT_CONNECTION_TIMEOUT_MILLIS)
        .ttlMillis(DEFAULT_TTL_MILLIS);
  }

  public abstract int connectionPoolSize();

  public abstract int maxPooledPerRouteSize();

  public abstract int socketTimeoutMillis();

  public abstract int connectionTimeoutMillis();

  public abstract int ttlMillis();

  @Nullable
  public abstract SSLContext sslContext();

  @Nullable
  public abstract KeyStore trustStore();

  @Nullable
  public abstract String correlationIdName();

  @Nullable
  public abstract HostnameVerifier verifier();

  /**
   * Returns a builder with same property values as this; allowing modification of some values.
   */
  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder connectionPoolSize(int connectionPoolSize);

    public abstract Builder maxPooledPerRouteSize(int maxPooledRouteSize);

    public abstract Builder socketTimeoutMillis(int socketTimeoutMillis);

    public abstract Builder connectionTimeoutMillis(int connectionTimeoutMillis);

    public abstract Builder ttlMillis(int ttlMillis);

    public abstract Builder sslContext(SSLContext context);

    public abstract Builder trustStore(KeyStore key);

    public abstract Builder correlationIdName(String id);

    public abstract Builder verifier(HostnameVerifier verifier);

    public abstract BeadledomClientConfiguration build();
  }
}
