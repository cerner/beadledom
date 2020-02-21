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
   * Returns the correlationId from the context; null if a correlationId is not found.
   */
  @Nullable
  String getId();

  /**
   * Set the correlation id
   *
   * @param correlationId
   */
  @Nullable
  void setId(String correlationId);

  /**
   * Returns the value to use as a key for the correlation id when used in a key-value store.
   */
  String getKey();
}
