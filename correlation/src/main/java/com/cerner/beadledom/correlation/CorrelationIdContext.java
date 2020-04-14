package com.cerner.beadledom.correlation;

import java.io.Closeable;
import javax.annotation.Nullable;

/**
 * A context that provides access to the Correlation ID.
 */
public interface CorrelationIdContext {
  /**
   * Returns the current Correlation ID, {@code null} if no Correlation ID is in context.
   */
  @Nullable
  String get();

  /**
   * Sets the Correlation ID. If {@code correlationId} is null, a correlation id is generated.
   */
  void set(@Nullable String correlationId);

  /**
   * Sets the Correlation ID. If {@code correlationId} is null, a Correlation ID is generated. The
   * returned {@link Closeable} object will clear the Correlation ID when {@link Closeable#close()}
   * is called. This method is useful to use with try-with-resources when setting a Correlation ID
   * for the duration of the try block.
   */
  CorrelationIdCloseable setCloseable(@Nullable String correlationId);

  /**
   * Resets the Correlation ID to {@code null}.
   */
  void reset();

  /**
   * A Closeable object that resets the Correlation ID context when {@link Closeable#close()} is
   * called.
   */
  class CorrelationIdCloseable implements Closeable {

    private final CorrelationIdContext correlationIdContext;

    public CorrelationIdCloseable(CorrelationIdContext correlationIdContext) {
      this.correlationIdContext = correlationIdContext;
    }

    @Override
    public void close() {
      correlationIdContext.reset();
    }
  }
}
