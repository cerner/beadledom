package com.cerner.beadledom.correlation;

import javax.annotation.Nullable;

/**
 * A context for retrieving the correlationId.
 *
 * <p>Different implementations of this can be used depending on how the correlationId should be
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
  String getId();

  /**
   * Sets the correlation id.
   *
   * @param correlationId The correlation Id to set in the context. If {@code null} is set, a
   *                      correlation id will be generated and set.
   */
  void setId(@Nullable String correlationId);

  /**
   * Returns the value to use as a key for the correlation id when used in a key-value store.
   */
  String getKey();

  /**
   * Removes the correlation id from context.
   */
  void removeId();
}
