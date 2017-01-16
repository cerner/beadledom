package com.cerner.beadledom.client;

import javax.annotation.Nullable;

/**
 * A context for retrieving the correlationId.
 *
 * <p>Different implementations of this can be used depending on how the correlationId should be
 * retrieved.
 *
 * @author John Leacox
 * @since 1.0
 */
public interface CorrelationIdContext {
  /**
   * The default header name for the correlationId.
   */
  String DEFAULT_HEADER_NAME = "Correlation-Id";

  /**
   * The default MDC property name for the correlationId.
   *
   * @deprecated This property exists for legacy support of the original beadledom-1.0 release,
   *     which allows setting a different name for the MDC property value than the header value.
   *     Ideally the same name will be used for the header and MDC if MDC is used at all.
   */
  @Deprecated
  String DEFAULT_MDC_NAME = "Correlation-Id";

  /**
   * Returns the correlationId from the context; null if a correlationId is not found.
   */
  @Nullable
  String getCorrelationId();
}
