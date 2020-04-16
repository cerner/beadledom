package com.cerner.beadledom.correlation;

import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

/**
 * A context that provides access to the Correlation ID.
 */
public final class CorrelationContext {

  private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

  private CorrelationContext() {

  }

  /**
   * Creates a Correlation Context.
   */
  public static CorrelationContext create() {
    return new CorrelationContext();
  }

  /**
   * Returns the current Correlation ID, {@code null} if no Correlation ID is in context.
   */
  @Nullable
  public String getId() {
    return CorrelationContext.correlationId.get();
  }

  /**
   * Sets the Correlation ID. If {@code correlationId} is {@code null}, a correlation id is
   * generated.
   */
  public void setId(@Nullable String correlationId) {
    if (correlationId == null) {
      CorrelationContext.correlationId.set(UUID.randomUUID().toString());
    } else {
      CorrelationContext.correlationId.set(correlationId);
    }
  }

  /**
   * Sets the Correlation ID to {@code null}.
   */
  public void clearId() {
    CorrelationContext.correlationId.remove();
  }

  /**
   * Computes the result of the {@code task} with the correlation context ID being set to {@code
   * correlationId}. The correlation id is cleared from the context after the completion of the
   * task. If {@code correlationId} is {@code null}, a correlation ID is generated.
   */
  public <V> V withId(@Nullable String correlationId, Callable<V> task) throws Exception {
    try {
      setId(correlationId);
      return task.call();
    } finally {
      clearId();
    }
  }
}
