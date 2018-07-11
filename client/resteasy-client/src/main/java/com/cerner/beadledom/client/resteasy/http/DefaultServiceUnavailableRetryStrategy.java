package com.cerner.beadledom.client.resteasy.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default {@link ServiceUnavailableRetryStrategy} with a configurable retry interval.
 *
 * @author John Leacox
 * @since 1.0
 */
public class DefaultServiceUnavailableRetryStrategy
    implements ServiceUnavailableRetryStrategy {
  private static final Logger logger =
      LoggerFactory.getLogger(DefaultServiceUnavailableRetryStrategy.class);

  private final int retryIntervalMillis;

  /**
   * Creates an instance of {@link DefaultServiceUnavailableRetryStrategy} with the given retry
   * interval.
   */
  public DefaultServiceUnavailableRetryStrategy(int retryIntervalMillis) {
    this.retryIntervalMillis = retryIntervalMillis;
  }

  @Override
  public boolean retryRequest(
      HttpResponse httpResponse, int executionCount, HttpContext httpContext) {
    if (executionCount < 3 && isServiceUnavailableResponse(httpResponse)) {
      HttpClientContext context = HttpClientContext.adapt(httpContext);
      logger.info(
          "Retry " + executionCount + " for request for: "
              + context.getRequest().getRequestLine().getUri());
      return true;
    }
    return false;
  }

  private boolean isServiceUnavailableResponse(HttpResponse response) {
    return response.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE;
  }

  @Override
  public long getRetryInterval() {
    return retryIntervalMillis;
  }
}
