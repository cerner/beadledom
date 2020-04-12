package com.cerner.beadledom.jaxrs;

import com.cerner.beadledom.correlation.CorrelationContext;
import com.cerner.beadledom.jaxrs.provider.CorrelationIdFilter;
import com.cerner.beadledom.jaxrs.provider.CorrelationIdHeader;
import com.cerner.beadledom.jaxrs.provider.CorrelationIdMdc;
import com.google.inject.Inject;
import javax.annotation.Nullable;
import javax.inject.Provider;

/**
 * A guice provider for {@link CorrelationIdFilter}.
 *
 * @author John Leacox
 */
class CorrelationIdFilterProvider implements Provider<CorrelationIdFilter> {
  @CorrelationIdHeader
  @Nullable
  @Inject(optional = true)
  String correlationIdHeader;

  @CorrelationIdMdc
  @Nullable
  @Inject(optional = true)
  String correlationIdMdc;

  @Nullable
  @Inject(optional = true)
  CorrelationContext correlationContext;

  @Override
  public CorrelationIdFilter get() {
    return new CorrelationIdFilter(correlationIdHeader, correlationIdMdc, correlationContext);
  }
}
