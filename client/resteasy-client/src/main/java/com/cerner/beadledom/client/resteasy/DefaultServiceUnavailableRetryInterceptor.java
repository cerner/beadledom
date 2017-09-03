package com.cerner.beadledom.client.resteasy;

import static okhttp3.internal.Util.closeQuietly;

import java.io.IOException;
import java.io.InterruptedIOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An OkHttp {@link Interceptor} for performing request retries on a 503 response.
 *
 * <p>This is a very basic implementation that performs up to 3 retries and is not configurable in
 * any way.
 *
 * @author John Leacox
 * @since 2.6
 */
class DefaultServiceUnavailableRetryInterceptor implements Interceptor {
  private static final Logger logger =
      LoggerFactory.getLogger(DefaultServiceUnavailableRetryInterceptor.class);

  private final DefaultServiceUnavailableRetryStrategy retryStrategy =
      new DefaultServiceUnavailableRetryStrategy();

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    int currentRetryCount = 1;
    while (true) {
      Response response = chain.proceed(request);

      if (!retryStrategy.shouldRetry(response, currentRetryCount)) {
        return response;
      }

      long retryInterval = retryStrategy.getRetryIntervalMillis(currentRetryCount);
      try {
        logger.info("Retry " + currentRetryCount + " for request for: " + request.url());
        logger.debug("Retrying request after waiting " + retryInterval);
        Thread.sleep(retryInterval);
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new InterruptedIOException();
      }

      // Close out the response body prior to trying again.
      closeQuietly(response.body());
      request = response.request();
    }
  }
}
