package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientBuilder;
import com.cerner.beadledom.client.BeadledomClientConfiguration;
import com.cerner.beadledom.client.CorrelationIdContext;
import com.cerner.beadledom.client.CorrelationIdFilter;
import com.cerner.beadledom.client.resteasy.http.DefaultServiceUnavailableRetryStrategy;
import com.cerner.beadledom.client.resteasy.http.X509HostnameVerifierAdapter;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;

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
  public BeadledomResteasyClientBuilder setSocketTimeout(int socketTimeout, TimeUnit timeUnit) {
    long millis = timeUnit.toMillis(socketTimeout);
    if (millis > Integer.MAX_VALUE || millis < 0) {
      throw new IllegalArgumentException(
          "Socket timeout must be smaller than Integer.MAX_VALUE when converted to milliseconds"
      );
    }
    return readTimeout((int) millis, TimeUnit.MILLISECONDS);
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
    return connectTimeout((int) millis, TimeUnit.MILLISECONDS);
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
  public ClientBuilder executorService(ExecutorService executorService) {
    resteasyClientBuilder.executorService(executorService);
    return this;
  }

  @Override
  public ClientBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
    resteasyClientBuilder.scheduledExecutorService(scheduledExecutorService);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder connectTimeout(long timeout, TimeUnit unit) {
    long millis = unit.toMillis(timeout);
    if (millis > Integer.MAX_VALUE || millis < 0) {
      throw new IllegalArgumentException(
          "Connect timeout must be smaller than Integer.MAX_VALUE when converted to milliseconds"
      );
    }

    this.clientConfigBuilder.connectionTimeoutMillis((int) millis);
    return this;
  }

  @Override
  public BeadledomResteasyClientBuilder readTimeout(long timeout, TimeUnit unit) {
    long millis = unit.toMillis(timeout);
    if (millis > Integer.MAX_VALUE || millis < 0) {
      throw new IllegalArgumentException(
          "Read timeout must be smaller than Integer.MAX_VALUE when converted to milliseconds");
    }

    this.clientConfigBuilder.socketTimeoutMillis((int) millis);
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
    SocketConfig socketConfig = SocketConfig.custom()
        .setSoTimeout(clientConfig.socketTimeoutMillis())
        .build();

    PoolingHttpClientConnectionManager connectionManager =
        new PoolingHttpClientConnectionManager(clientConfig.ttlMillis(), TimeUnit.SECONDS);
    connectionManager.setMaxTotal(clientConfig.connectionPoolSize());
    connectionManager.setDefaultMaxPerRoute(clientConfig.maxPooledPerRouteSize());
    connectionManager.setDefaultSocketConfig(socketConfig);

    RequestConfig requestConfig =
        RequestConfig.custom()
            .setConnectionRequestTimeout(clientConfig.connectionTimeoutMillis())
            .setConnectTimeout(clientConfig.connectionTimeoutMillis())
            .setSocketTimeout(clientConfig.socketTimeoutMillis())
            .build();

    // DefaultRedirectStrategy will only redirect HEAD and GET requests.
    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    HttpClientBuilder httpClientBuilder =
        HttpClientBuilder.create()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .setDefaultSocketConfig(socketConfig)
            .setRedirectStrategy(redirectStrategy)
            .setRetryHandler(
                new StandardHttpRequestRetryHandler(3, true))
            .setServiceUnavailableRetryStrategy(
                new DefaultServiceUnavailableRetryStrategy(DEFAULT_RETRY_INTERVAL_MILLIS));

    X509HostnameVerifier verifier = null;
    if (clientConfig.verifier() != null) {
      verifier = X509HostnameVerifierAdapter.adapt(clientConfig.verifier());
    }

    if (verifier != null) {
      httpClientBuilder.setHostnameVerifier(verifier);
    }

    try {
      SSLConnectionSocketFactory sslConnectionSocketFactory;
      SSLContext configuredContext = clientConfig.sslContext();
      if (configuredContext != null) {
        sslConnectionSocketFactory = new SSLConnectionSocketFactory(configuredContext, verifier);
      } else if (clientKeyStore != null || clientConfig.trustStore() != null) {
        SSLContext tlsContext = SSLContexts.custom()
            .useProtocol(SSLConnectionSocketFactory.TLS)
            .setSecureRandom(null)
            .loadKeyMaterial(
                clientKeyStore,
                clientPrivateKeyPassword != null ? clientPrivateKeyPassword.toCharArray() : null)
            .loadTrustMaterial(clientConfig.trustStore())
            .build();
        sslConnectionSocketFactory = new SSLConnectionSocketFactory(tlsContext, verifier);
      } else {
        SSLContext tlsContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
        tlsContext.init(null, null, null);
        sslConnectionSocketFactory = new SSLConnectionSocketFactory(tlsContext, verifier);
      }

      httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
    } catch (Exception e) {
      throw new RuntimeException("An error occurred configuring SSL", e);
    }

    CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

    HttpContext context = new BasicHttpContext();
    context.setAttribute(HttpClientContext.REQUEST_CONFIG, requestConfig);

    return new ApacheHttpClient43Engine(closeableHttpClient, context);
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
