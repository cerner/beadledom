package com.cerner.beadledom.correlation;

import java.util.UUID;
import javax.annotation.Nullable;

public class CorrelationIdContextImpl implements CorrelationIdContext {

  private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

  @Nullable
  @Override
  public String get() {
    return correlationId.get();
  }

  @Override
  public void set(@Nullable String correlation) {
    if (correlation == null) {
      correlationId.set(UUID.randomUUID().toString());
    } else {
      correlationId.set(correlation);
    }
  }

  @Override
  public CorrelationIdContext.CorrelationIdCloseable setCloseable(@Nullable String correlationId) {
    set(correlationId);
    return new CorrelationIdContext.CorrelationIdCloseable(this);
  }

  @Override
  public void reset() {
    correlationId.remove();
  }
}
