package com.cerner.beadledom.client;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * Client request filter for capturing the Correlation Id.
 *
 * @author John Leacox
 */
@Provider
@Priority(100000)
public class CaptureCorrelationIdFilter implements ClientRequestFilter {
  private String correlationId;

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    correlationId = requestContext.getHeaderString(CorrelationIdContext.DEFAULT_HEADER_NAME);
  }

  public String getCapturedCorrelationId() {
    return correlationId;
  }
}
