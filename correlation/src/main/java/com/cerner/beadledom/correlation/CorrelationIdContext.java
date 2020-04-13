package com.cerner.beadledom.correlation;

import java.io.Closeable;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A context that provides access to the Correlation ID.
 */
public class CorrelationIdContext {

  private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

  private CorrelationIdContext() {
  }

  /**
   * Returns the current Correlation ID, {@code null} if no Correlation ID is in context.
   */
  @Nullable
  public static String get() {
    return correlationId.get();
  }

  /**
   * Sets the Correlation ID. If {@code correlation} is null, a correlation id is generated.
   */
  public static void set(@Nullable String correlation) {
    if (correlation == null) {
      correlationId.set(UUID.randomUUID().toString());
    } else {
      correlationId.set(correlation);
    }
  }

  /**
   * Sets the Correlation ID. If {@code correlationId} is null, a Correlation ID is generated. The
   * returned {@link Closeable} object will clear the Correlation ID when {@link Closeable#close()}
   * is called. This method is useful to use with try-with-resources when setting a Correlation ID
   * for the duration of the try block.
   */
  public static CorrelationIdCloseable setCloseable(@Nullable String correlationId) {
    set(correlationId);
    return new CorrelationIdCloseable();
  }

  /**
   * Resets the Correlation ID to {@code null}.
   */
  public static void reset() {
    correlationId.remove();
  }

  /**
   * A Closeable object that resets the Correlation ID context when {@link Closeable#close()} is
   * called.
   */
  public static class CorrelationIdCloseable implements Closeable {

    private CorrelationIdCloseable() {
    }

    @Override
    public void close() {
      CorrelationIdContext.reset();
    }
  }
}
