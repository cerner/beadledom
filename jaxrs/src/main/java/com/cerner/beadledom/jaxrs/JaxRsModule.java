package com.cerner.beadledom.jaxrs;

import com.cerner.beadledom.jaxrs.provider.CorrelationIdFilter;
import com.cerner.beadledom.jaxrs.provider.ForwardedHeaderFilter;
import com.google.inject.AbstractModule;
import javax.inject.Singleton;

/**
 * A guice module for providing jax-rs specific bindings.
 *
 * <p>Binds:
 * <ul>
 *   <li>{@link ForwardedHeaderFilter}</li>
 * </ul>
 * </p>
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link CorrelationIdFilter}</li>
 * </ul>
 *
 * @author John Leacox
 */
public class JaxRsModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CorrelationIdFilter.class).toProvider(CorrelationIdFilterProvider.class)
        .in(Singleton.class);
    bind(ForwardedHeaderFilter.class).in(Singleton.class);
  }
}
