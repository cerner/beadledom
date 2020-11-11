package com.cerner.beadledom.client.resteasy;

import java.io.IOException;
import java.util.Random;
import javax.net.ssl.SSLException;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

/**
 * A {@link StandardHttpRequestRetryHandler} that adds an exponential back-off to retries.
 *
 * @author John Leacox
 * @since 3.5
 * @deprecated As of 3.6, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
class ExponentialBackoffHttpRequestRetryHandler extends StandardHttpRequestRetryHandler {
  private static final int INITIAL_DELAY = 100;
  private static final int BACKOFF_FACTOR = 2;
  private static final Random random = new Random();

  ExponentialBackoffHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
    super(retryCount, requestSentRetryEnabled);
  }

  @Override
  public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
    IOException cause = exception;
    if (exception instanceof SSLException
        && exception.getCause() != null
        && exception.getCause() instanceof IOException) {
      cause = (IOException)exception.getCause();
    }
    if (!super.retryRequest(cause, executionCount, context)) {
      return false;
    }

    // Adding the delay here is not ideal, but there doesn't appear to be a better place in Apache
    // httpclient similar to ServiceUnavailableRetryStrategy#getRetryInterval.
    try {
      Thread.sleep(calculateRetryDelay(executionCount));
    } catch (InterruptedException e) {
      // ignore
    }

    return true;
  }

  // VisibleForTesting
  int calculateRetryDelay(int executionCount) {
    double backoffMultiplier = Math.pow(BACKOFF_FACTOR, executionCount - 1);
    return (int) (INITIAL_DELAY * backoffMultiplier + jitter());
  }

  private int jitter() {
    return random.nextInt(5);
  }
}
