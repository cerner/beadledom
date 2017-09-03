package com.cerner.beadledom.client.resteasy;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

import okhttp3.Response;
import okhttp3.internal.http.UnrepeatableRequestBody;

class DefaultServiceUnavailableRetryStrategy {
  private static final int MAX_RETRIES = 3;

  boolean shouldRetry(Response response, int currentRetryCount) {
    if (currentRetryCount > MAX_RETRIES) {
      return false;
    }

    if (response.request().body() instanceof UnrepeatableRequestBody) {
      return false;
    }

    int responseCode = response.code();

    return responseCode == HTTP_UNAVAILABLE;
  }

  long getRetryIntervalMillis(int currentRetryCount) {
    return 1000L;
  }
}
