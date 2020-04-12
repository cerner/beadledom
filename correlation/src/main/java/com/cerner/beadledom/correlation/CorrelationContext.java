package com.cerner.beadledom.correlation;

import javax.annotation.Nullable;

/**
 * A context for retrieving the correlation id.
 *
 * <p>Different implementations of this can be used depending on how the correlation id should be
 * retrieved.
 *
 * @author John Leacox
 * @author Nathan Schile
 * @since 3.5
 */
public interface CorrelationContext {

  /**
   * Returns the correlationId from the context; null if a correlation id is not found.
   */
  @Nullable
  String getCorrelationId();

  /**
   * Sets the correlation id.
   *
   * @param correlationId The correlation Id to set in the context. If {@code null} is set, a
   *                      correlation id will be generated and set.
   */
  void setCorrelationId(@Nullable String correlationId);

  /**
   * Resets the correlation id to {@code null}.
   */
  void resetCorrelationId();
}
