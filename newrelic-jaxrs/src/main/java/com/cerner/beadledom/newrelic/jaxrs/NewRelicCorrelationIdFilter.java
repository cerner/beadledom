package com.cerner.beadledom.newrelic.jaxrs;

import com.cerner.beadledom.newrelic.NewRelicApi;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * The NewRelicCorrelationIdFilter reads the correlation id header/property from the request, then
 * adds it to the New Relic transaction as a custom property. The priority of the filter was set to
 * 3050 to invoke this filter after {@link com.cerner.beadledom.jaxrs.provider.CorrelationIdFilter}
 *
 * @author Nathan Schile
 * @since 2.8
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR + 50)
public class NewRelicCorrelationIdFilter implements ContainerRequestFilter {

  private final String headerName;
  private final NewRelicApi newRelic;

  /**
   * Creates a new {@link NewRelicCorrelationIdFilter} with the provided header name.
   *
   * @param headerName the correlation id header name to be used in request headers
   * @param newRelic the New Relic API
   */
  public NewRelicCorrelationIdFilter(String headerName, NewRelicApi newRelic) {
    if (headerName == null) {
      throw new NullPointerException("headerName: null");
    }
    if (newRelic == null) {
      throw new NullPointerException("newRelic: null");
    }
    this.headerName = headerName;
    this.newRelic = newRelic;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String correlationId = requestContext.getHeaderString(headerName);
    if (correlationId == null) {
      correlationId = (String) requestContext.getProperty(headerName);
    }

    if (correlationId != null) {
      newRelic.addCustomParameter(headerName, correlationId);
    }
  }
}
