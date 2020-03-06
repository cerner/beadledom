package com.cerner.beadledom.jaxrs.provider;

import com.cerner.beadledom.correlation.CorrelationContext;
import com.cerner.beadledom.correlation.TheadLocalCorrelationContext;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.MDC;

/**
 * The CorrelationIdFilter reads the correlation id header from the request, adds it to the
 * response headers, and adds it to the slf4j Mapped Diagnostic Context (MDC).
 *
 * <p>The correlation id will be taken from the correlation id request header if present, or
 * generated if absent. The id will also be added to the response and to the MDC for logging.
 *
 * <p>The default header and mdc key name is {@code Correlation-Id}. However, both names are
 * configurable through the {@link #CorrelationIdFilter(String, String)} constructor.
 *
 * <p>To include the correlation id in the catalina.out file, update the log format to include
 * `%X{Correlation-Id}` replacing the name with your configured name.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private static final String DEFAULT_HEADER_NAME = "Correlation-Id";
  private static final String DEFAULT_MDC_NAME = "Correlation-Id";
  private final String headerName;
  private final String mdcName;
  private final CorrelationContext correlationContext;

  /**
   * Creates a new {@link CorrelationIdFilter} with the provided correlation id header.
   *
   * @param headerName the correlation id header name to be used in request/response headers, if
   *     null the default value will be used
   * @param mdcName the correlation id name to use in the {@link MDC}, if null the default value
   *     will be used
   */
  public CorrelationIdFilter(@Nullable String headerName, @Nullable String mdcName) {
    this(headerName, mdcName, TheadLocalCorrelationContext.create(headerName));
  }

  /**
   * Creates a new {@link CorrelationIdFilter} with the provided correlation id header.
   *
   * @param headerName the correlation id header name to be used in request/response headers, if
   *     null the default value will be used
   * @param mdcName the correlation id name to use in the {@link MDC}, if null the default value
   *     will be used
   * @param correlationContext  the correlation context.
   */
  public CorrelationIdFilter(@Nullable String headerName, @Nullable String mdcName, CorrelationContext correlationContext) {
    this.headerName = Optional.ofNullable(headerName).orElse(DEFAULT_HEADER_NAME);
    this.mdcName = Optional.ofNullable(mdcName).orElse(DEFAULT_MDC_NAME);
    this.correlationContext = correlationContext;

  }

  /**
   * Creates a new {@link CorrelationIdFilter} with the default correlation id header/mdc name of
   * 'Correlation-Id'.
   */
  public CorrelationIdFilter() {
    this(null, null);
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String correlationId = requestContext.getHeaderString(headerName);
    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
    }
    requestContext.setProperty(mdcName, correlationId);
    MDC.put(mdcName, correlationId);
    correlationContext.setId(correlationId);
  }

  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    MDC.remove(mdcName);
    correlationContext.removeId();
    String correlationId = (String) requestContext.getProperty(mdcName);
    if (correlationId == null) { // Can happen if there are oauth issues.
      correlationId = UUID.randomUUID().toString();
    }
    responseContext.getHeaders().add(headerName, correlationId);
  }
}
