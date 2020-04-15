package com.cerner.beadledom.correlation;

import java.util.concurrent.Callable;
import javax.annotation.Nullable;

/**
 * A context that provides access to the Correlation ID.
 */
public interface CorrelationContext {

  /**
   * Returns the current Correlation ID, {@code null} if no Correlation ID is in context.
   */
  @Nullable
  String getId();

  /**
   * Sets the Correlation ID. If {@code correlationId} is {@code null}, a correlation id is
   * generated.
   */
  void setId(@Nullable String correlationId);

  /**
   * Sets the Correlation ID to {@code null}.
   */
  void clearId();

  /**
   * Computes the result of the {@code task} with the correlation context ID being set to {@code
   * correlationId}. The correlation id is cleared from the context after the completion of the
   * task. If {@code correlationId} is {@code null}, a correlation ID is generated.
   */
  default <V> V withId(@Nullable String correlationId, Callable<V> task) throws Exception {
    try {
      setId(correlationId);
      return task.call();
    } finally {
      clearId();
    }
  }


}
