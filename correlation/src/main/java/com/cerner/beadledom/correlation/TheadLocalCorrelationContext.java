package com.cerner.beadledom.correlation;

import java.util.UUID;
import javax.annotation.Nullable;

public class TheadLocalCorrelationContext implements CorrelationContext {

  private ThreadLocal<String> correlationId = new ThreadLocal<>();
  private final String key;

  public TheadLocalCorrelationContext(String key) {
    this.key = key;
  }

  @Nullable
  @Override
  public String getId() {
    return correlationId.get();
  }

  @Override
  public void setId(@Nullable String correlationId) {
    if (correlationId == null) {
      this.correlationId.set(UUID.randomUUID().toString());
    } else {
      this.correlationId.set(correlationId);
    }
  }

  @Override
  public String getKey() {
    return key;
  }
}
