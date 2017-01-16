package com.cerner.beadledom.lifecycle.legacy;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * A guice module for configuring the legacy lifecycle injector.
 *
 * @author John Leacox
 * @since 1.2
 * @deprecated The Governator lifecycle implementation should be used instead if possible
 */
@Deprecated
class BeadledomLifecycleModule extends AbstractModule {
  private final LifecycleProvisionListener listener = new LifecycleProvisionListener();

  @Override
  protected void configure() {
    requireBinding(LifecycleShutdownManager.class);

    requestStaticInjection(LifecycleProvisionListener.class);
    bind(LifecycleProvisionListener.class).toInstance(listener);
    bindListener(Matchers.any(), listener);
  }
}
