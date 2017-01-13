package com.cerner.beadledom.client.jaxrs;

import com.cerner.beadledom.client.CorrelationIdContext;

/**
 * A test CorrelationIdContext.
 *
 * @author John Leacox
 */
public class TestCorrelationIdContext implements CorrelationIdContext {
  private String correlationId = null;

  public void clear() {
    correlationId = null;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  @Override
  public String getCorrelationId() {
    return correlationId;
  }
}
