package com.cerner.beadledom.correlation;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A tread local implementation of {@link CorrelationContext}.
 */
public class TheadLocalCorrelationContext implements CorrelationContext {

  private ThreadLocal<String> correlationId = new ThreadLocal<>();
  private final String key;

  /**
   * Constructs a TheadLocalCorrelationContext.
   * @param key The value to use as a key for the correlation id when used in a key-value store.
   */
  private TheadLocalCorrelationContext(String key) {
    Objects.requireNonNull(key, "key:null");
    this.key = key;
  }

  public static TheadLocalCorrelationContext create() {
    return TheadLocalCorrelationContext.create("Cerner-Correlation-Id");
  }

  /**
   * Creates an instance of {@link TheadLocalCorrelationContext}.
   *
   * @param key The value to use as a key for the correlation id when used in a key-value store.
   */
  public static TheadLocalCorrelationContext create(String key) {
    return new TheadLocalCorrelationContext(key);
  }

  @Nullable
  @Override
  public String getId() {
    return correlationId.get();
  }

  @Override
  public void removeId() {
    correlationId.remove();
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
