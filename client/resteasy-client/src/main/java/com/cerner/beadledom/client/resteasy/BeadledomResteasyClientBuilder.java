package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientBuilder;
import com.cerner.beadledom.client.BeadledomClientConfiguration;
import com.cerner.beadledom.client.CorrelationIdContext;
import com.cerner.beadledom.client.CorrelationIdFilter;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import net.ltgt.resteasy.client.okhttp3.OkHttpClientEngine;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * Abstraction for creating Clients.
 *
 * <p>Delegates to {@link ResteasyClientBuilder} and additionally adds a default implementation of
 * {@link CorrelationIdFilter}.
 *
 * @author John Leacox
 * @author Sundeep Paruvu
 * @since 1.0
 */
public class BeadledomResteasyClientBuilder extends BeadledomClientBuilder {
  private static final int DEFAULT_RETRY_INTERVAL_MILLIS = 1000;

  private final ResteasyClientBuilder resteasyClientBuilder;
  private BeadledomClientConfiguration.Builder clientConfigBuilder;
  private ClientHttpEngine httpEngine;

  private KeyStore clientKeyStore;
  private String clientPrivateKeyPassword;

  /**
   * Needed for methods that look for class implementations using SPI. Avoid using the default
   * constructor.
   */
  public BeadledomResteasyClientBuilder() {
    this.resteasyClientBuilder = new ResteasyClientBuilder();
    this.clientConfigBuilder = BeadledomClientConfiguration.builder();
  }

  public BeadledomResteasyClientBuilder(BeadledomClientConfiguration clientConfiguration) {
    this.resteasyClientBuilder = new ResteasyClientBuilder();
    this.clientConfigBuilder = BeadledomClientConfiguration.builder(clientConfiguration);
  }

  public static BeadledomResteasyClientBuilder newBuilder(
      BeadledomClientConfiguration clientConfiguration) {
    return new BeadledomResteasyClientBuilder(clientConfiguration);
  }

  public static BeadledomResteasyClientBuilder newBuilder() {
    return newBuilder(BeadledomClientConfiguration.builder().build());
  }

  public static BeadledomResteasyClient newClient() {
    return newBuilder().build();
  }

  public static BeadledomClient newClient(final Configuration configuration) {
    return newBuilder().withConfig(configuration).build();
  }

  public BeadledomClientConfiguration getBeadledomClientConfiguration() {
    return clientConfigBuilder.build();
  }

  /**
   * Set the backing {@link ClientHttpEngine} to be used for all http calls.
   *
   * @return this builder
   */
  public BeadledomResteasyClientBuilder setHttpEngine(ClientHttpEngine httpEngine) {
    this.httpEngine = httpEngine;
    return this;
  }

  /**
   * Sets the default connection pool size to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder setConnectionPoolSize(int connectionPoolSize) {
    if (connectionPoolSize < 0) {
      throw new IllegalArgumentException(
          "Connection pool size cannot be negative");
    }
    this.clientConfigBuilder.connectionPoolSize(connectionPoolSize);
    return this;
  }

  /**
   * Sets the default max connection pool size per route to be used if a {@link ClientHttpEngine}
   * isn't specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder setMaxPooledPerRouteSize(int maxPooledPerRouteSize) {
    if (maxPooledPerRouteSize < 0) {
      throw new IllegalArgumentException(
          "max connection pool size per route cannot be negative");
    }
    this.clientConfigBuilder.maxPooledPerRouteSize(maxPooledPerRouteSize);
    return this;
  }

  /**
   * Sets the default socket timeout to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder setSocketTimeout(
      int socketTimeout, TimeUnit timeUnit) {
    long millis = timeUnit.toMillis(socketTimeout);
    if (millis > Integer.MAX_VALUE || millis < 0) {
      throw new IllegalArgumentException(
          "Socket timeout must be smaller than Integer.MAX_VALUE when converted to milliseconds");
    }

    this.clientConfigBuilder.socketTimeoutMillis((int) millis);
    return this;
  }

  /**
   * Sets the default connection timeout to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder setConnectionTimeout(
      int connectionTimeout, TimeUnit timeUnit) {
    long millis = timeUnit.toMillis(connectionTimeout);
    if (millis > Integer.MAX_VALUE || millis < 0) {
      throw new IllegalArgumentException(
          "Connection timeout must be smaller than Integer.MAX_VALUE when converted to milliseconds"
      );
    }
    this.clientConfigBuilder.connectionTimeoutMillis((int) millis);
    return this;
  }

  /**
   * Sets the default TTL to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder setTtl(int ttl, TimeUnit timeUnit) {
    long millis = timeUnit.toMillis(ttl);
    if (millis > Integer.MAX_VALUE || millis < 0) {
      throw new IllegalArgumentException(
          "TTL must be smaller than Integer.MAX_VALUE when converted to milliseconds");
    }

    this.clientConfigBuilder.ttlMillis((int) millis);
    return this;
  }

  /**
   * Sets the default SSL Context to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * <p>{@inheritDoc}
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder sslContext(SSLContext sslContext) {
    this.clientConfigBuilder.sslContext(sslContext);
    return this;
  }

  /**
   * Sets the default SSL trust store to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * <p>{@inheritDoc}
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder trustStore(KeyStore trustStore) {
    this.clientConfigBuilder.trustStore(trustStore);
    return this;
  }

  /**
   * Sets the default hostname verifier to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * <p>{@inheritDoc}
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder hostnameVerifier(HostnameVerifier verifier) {
    this.clientConfigBuilder.verifier(verifier);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder setCorrelationIdName(String correlationIdName) {
    this.clientConfigBuilder.correlationIdName(correlationIdName);
    return this;
  }

  @Override
  public BeadledomResteasyClient build() {
    BeadledomClientConfiguration clientConfig = clientConfigBuilder.build();

    CorrelationIdContext correlationIdContext =
        new ResteasyCorrelationIdContext(
            clientConfig.correlationIdName(),
            clientConfig.correlationIdName());

    CorrelationIdFilter correlationIdFilter =
        new CorrelationIdFilter(correlationIdContext, clientConfig.correlationIdName());

    resteasyClientBuilder.register(correlationIdFilter);

    if (httpEngine == null) {
      httpEngine = initDefaultHttpEngine(clientConfig);
    }

    resteasyClientBuilder.httpEngine(httpEngine);
    return BeadledomResteasyClient.create(resteasyClientBuilder.build());
  }

  private ClientHttpEngine initDefaultHttpEngine(BeadledomClientConfiguration clientConfig) {
    ConnectionPool connectionPool =
        new ConnectionPool(
            clientConfig.connectionPoolSize(), clientConfig.ttlMillis(), TimeUnit.MILLISECONDS);

    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
        .connectTimeout(clientConfig.connectionTimeoutMillis(), TimeUnit.MILLISECONDS)
        .readTimeout(clientConfig.socketTimeoutMillis(), TimeUnit.MILLISECONDS)
        .writeTimeout(clientConfig.socketTimeoutMillis(), TimeUnit.MILLISECONDS)
        .connectionPool(connectionPool)
        .addInterceptor(new DefaultServiceUnavailableRetryInterceptor());

    HostnameVerifier verifier = clientConfig.verifier();
    if (verifier != null) {
      okHttpClientBuilder.hostnameVerifier(verifier);
    }

    SSLSocketFactory sslSocketFactory = null;
    X509TrustManager x509TrustManager = null;
    try {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());

      SSLContext configuredContext = clientConfig.sslContext();
      if (configuredContext != null) {
        sslSocketFactory = configuredContext.getSocketFactory();

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
          throw new IllegalStateException("Unexpected default trust managers:"
              + Arrays.toString(trustManagers));
        }

        x509TrustManager = (X509TrustManager) trustManagers[0];
      } else if (clientKeyStore != null || clientConfig.trustStore() != null) {

        KeyManagerFactory keyManagerFactory =
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(
            clientKeyStore,
            clientPrivateKeyPassword != null
                ? clientPrivateKeyPassword.toCharArray()
                : null);

        trustManagerFactory.init(clientConfig.trustStore());

        SSLContext tlsContext = SSLContext.getInstance("TLS");
        tlsContext
            .init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        sslSocketFactory = tlsContext.getSocketFactory();

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
          throw new IllegalStateException("Unexpected default trust managers:"
              + Arrays.toString(trustManagers));
        }

        x509TrustManager = (X509TrustManager) trustManagers[0];
      }
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }

    if (sslSocketFactory != null) {
      okHttpClientBuilder.sslSocketFactory(sslSocketFactory, x509TrustManager);
    }

    OkHttpClient client = okHttpClientBuilder.build();

    return new OkHttpClientEngine(client);
  }

  @Override
  public BeadledomResteasyClientBuilder withConfig(Configuration config) {
    resteasyClientBuilder.withConfig(config);
    return this;
  }

  /**
   * Sets the default SSL key store to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * <p>{@inheritDoc}
   *
   * @return this builder
   */
  @Override
  public BeadledomResteasyClientBuilder keyStore(KeyStore keyStore, char[] password) {
    this.clientKeyStore = keyStore;
    this.clientPrivateKeyPassword = new String(password);
    return this;
  }

  /**
   * Sets the default SSL key store to be used if a {@link ClientHttpEngine} isn't
   * specified via {@link #setHttpEngine(ClientHttpEngine)}.
   *
   * <p>If a {@link ClientHttpEngine} is specified via {@link #setHttpEngine(ClientHttpEngine)},
   * then this property will be ignored.
   *
   * <p>{@inheritDoc}
   *
   * @return this builder
   */
  @Override
  public ClientBuilder keyStore(final KeyStore keyStore, final String password) {
    this.clientKeyStore = keyStore;
    this.clientPrivateKeyPassword = password;
    return this;
  }

  @Override
  public Configuration getConfiguration() {
    return resteasyClientBuilder.getConfiguration();
  }

  @Override
  public BeadledomResteasyClientBuilder property(String name, Object value) {
    resteasyClientBuilder.property(name, value);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(Class<?> componentClass) {
    resteasyClientBuilder.register(componentClass);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(Class<?> componentClass, int priority) {
    resteasyClientBuilder.register(componentClass, priority);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
    resteasyClientBuilder.register(componentClass, contracts);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(
      Class<?> componentClass, Map<Class<?>, Integer> contracts) {
    resteasyClientBuilder.register(componentClass, contracts);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(Object component) {
    resteasyClientBuilder.register(component);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(Object component, int priority) {
    resteasyClientBuilder.register(component, priority);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(Object component, Class<?>... contracts) {
    resteasyClientBuilder.register(component, contracts);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder register(
      Object component, Map<Class<?>, Integer> contracts) {
    resteasyClientBuilder.register(component, contracts);
    return this;
  }
}
