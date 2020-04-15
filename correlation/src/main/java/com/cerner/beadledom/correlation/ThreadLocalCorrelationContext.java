package com.cerner.beadledom.correlation;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Correlation context that stores the correlation context in {@link ThreadLocal}.
 */
public class ThreadLocalCorrelationContext implements CorrelationContext {

  private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

  @Nullable
  @Override
  public String getId() {
    return correlationId.get();
  }

  @Override
  public void setId(@Nullable String correlation) {
    if (correlation == null) {
      correlationId.set(UUID.randomUUID().toString());
    } else {
      correlationId.set(correlation);
    }
  }

  @Override
  public void clearId() {
    correlationId.remove();
  }
}
