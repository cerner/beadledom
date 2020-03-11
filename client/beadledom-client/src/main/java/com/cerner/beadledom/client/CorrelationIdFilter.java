package com.cerner.beadledom.client;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * Filter for adding correlation id to all requests.
 *
 * <p>This filter will use a {@link CorrelationIdContext} to check if a correlation id is already
 * available within the context. If there is already a correlation id available, the existing one
 * will be used. If a correlation id is not available, a new one will be generated and used.
 *
 * @author John Leacox
 * @since 1.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationIdFilter implements ClientRequestFilter {
  private final String headerName;
  private final CorrelationIdContext correlationIdContext;

  /**
   * Creates a new instance of {@link CorrelationIdFilter}.
   *
   * @param correlationIdContext the correlationIdContext to retrieve the correlationId from
   * @param headerName the header name to use for the correlationId; pass null to use the default
   */
  public CorrelationIdFilter(
      CorrelationIdContext correlationIdContext, @Nullable String headerName) {
    if (correlationIdContext == null) {
      throw new NullPointerException("correlationIdContext:null");
    }
    this.correlationIdContext = correlationIdContext;
    this.headerName = headerName != null ? headerName : CorrelationIdContext.DEFAULT_HEADER_NAME;
  }

  /**
   * Creates a new {@link CorrelationIdFilter} with the default correlation id header name of
   * 'Correlation-Id'.
   */
  public CorrelationIdFilter(CorrelationIdContext correlationIdContext) {
    this(correlationIdContext, null);
  }

  /**
   * Returns the correlationId header name.
   */
  public String getCorrelationIdHeaderName() {
    return headerName;
  }

  @Override
  public void filter(ClientRequestContext clientRequestContext) throws IOException {
    String correlationId = correlationIdContext.getCorrelationId();
    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
    }

    clientRequestContext.getHeaders().putSingle(headerName, correlationId);
  }
}
