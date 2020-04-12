package com.cerner.beadledom.correlation;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A tread local implementation of {@link CorrelationContext}.
 */
public class TheadLocalCorrelationContext implements CorrelationContext {

  private ThreadLocal<String> correlationId = new ThreadLocal<>();

  private TheadLocalCorrelationContext() {

  }

  public static TheadLocalCorrelationContext create() {
    return new TheadLocalCorrelationContext();
  }

  @Nullable
  @Override
  public String getCorrelationId() {
    return correlationId.get();
  }

  @Override
  public void resetCorrelationId() {
    correlationId.remove();
  }

  @Override
  public void setCorrelationId(@Nullable String correlationId) {
    if (correlationId == null) {
      this.correlationId.set(UUID.randomUUID().toString());
    } else {
      this.correlationId.set(correlationId);
    }
  }
}
