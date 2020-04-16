package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.CorrelationIdContext;
import com.cerner.beadledom.correlation.CorrelationContext;
import javax.annotation.Nullable;
import org.slf4j.MDC;

/**
 * A Resteasy implementation of {@link CorrelationIdContext} that retrieves the correlationId from
 * the request headers if present, and falls back to the MDC for compatibility with
 * beadledom-jaxrs-1.0.
 *
 * @author John Leacox
 * @since 1.0
 * @deprecated As of 3.6, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
class ResteasyCorrelationIdContext implements CorrelationIdContext {
  private final String mdcName;
  private final CorrelationContext correlationContext;

  ResteasyCorrelationIdContext(
      @Nullable String mdcName,
      CorrelationContext correlationContext) {
    this.mdcName = mdcName != null ? mdcName : CorrelationIdContext.DEFAULT_MDC_NAME;
    this.correlationContext = correlationContext;
  }

  @Override
  public String getCorrelationId() {
    String correlationId = correlationContext.getId();
    if (correlationId != null) {
      return correlationId;
    } else {
      // Fall back to MDC to support beadledom-jaxrs 1.0. Retrieving from the headers is preferred.
      correlationId = MDC.get(mdcName);
    }

    return correlationId;
  }
}
