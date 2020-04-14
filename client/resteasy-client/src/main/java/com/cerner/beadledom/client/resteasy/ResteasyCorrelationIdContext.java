package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.CorrelationIdContext;
import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
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
  private final String headerName;
  private final String mdcName;
  private final com.cerner.beadledom.correlation.CorrelationIdContext correlationIdContext;

  ResteasyCorrelationIdContext(@Nullable String headerName, @Nullable String mdcName,
      com.cerner.beadledom.correlation.CorrelationIdContext correlationIdContext) {
    this.headerName = headerName != null ? headerName : CorrelationIdContext.DEFAULT_HEADER_NAME;
    this.mdcName = mdcName != null ? mdcName : CorrelationIdContext.DEFAULT_MDC_NAME;
    this.correlationIdContext = correlationIdContext;
  }

  @Override
  public String getCorrelationId() {

    String correlationId = correlationIdContext.get();
    if (correlationId != null) {
      return correlationId;
    }

    HttpHeaders headers = ResteasyProviderFactory.getContextData(HttpHeaders.class);
    if (headers != null) {
      correlationId = headers.getHeaderString(headerName);
      if (correlationId != null) {
        return correlationId;
      }
    }

    // Fall back to MDC to support beadledom-jaxrs 1.0. Retrieving from the headers is preferred.
    correlationId = MDC.get(mdcName);

    return correlationId;
  }
}
