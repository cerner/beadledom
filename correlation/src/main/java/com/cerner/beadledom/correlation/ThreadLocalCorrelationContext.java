package com.cerner.beadledom.correlation;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A thread local implementation of {@link CorrelationContext}.
 */
public class ThreadLocalCorrelationContext implements CorrelationContext {

  private final ThreadLocal<String> correlationId = new ThreadLocal<>();

  private ThreadLocalCorrelationContext() {
  }

  public static ThreadLocalCorrelationContext create() {
    return new ThreadLocalCorrelationContext();
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
