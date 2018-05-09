package com.cerner.beadledom.newrelic;

import com.cerner.beadledom.jaxrs.provider.CorrelationIdHeader;
import com.cerner.beadledom.newrelic.jaxrs.NewRelicCorrelationIdFilter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * A Guice module that provides the New Relic JAX-RS integration.
 *
 *
 * <p>Binds:
 * <ul>
 *    <li>{@link NewRelicApi} to {@link NewRelicApiImpl}</li>
 * </ul>
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link NewRelicCorrelationIdFilter}</li>
 * </ul>
 *
 * @author Nathan Schile
 * @since 2.8
 */
public class NewRelicJaxRsModule extends AbstractModule {

  private static final String DEFAULT_HEADER_NAME = "Correlation-Id";

  @Override
  protected void configure() {
    bind(NewRelicApi.class).to(NewRelicApiImpl.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  NewRelicCorrelationIdFilter provideNewRelicCorrelationIdFilter(
      @CorrelationIdHeader @Nullable String correlationIdHeader, NewRelicApi newRelic) {
    return new NewRelicCorrelationIdFilter(Optional.ofNullable(correlationIdHeader).orElse(DEFAULT_HEADER_NAME), newRelic);
  }

}
