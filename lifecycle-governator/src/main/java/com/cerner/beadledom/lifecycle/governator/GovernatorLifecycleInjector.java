package com.cerner.beadledom.lifecycle.governator;

import com.cerner.beadledom.lifecycle.DelegatingInjector;
import com.cerner.beadledom.lifecycle.GuiceLifecycleContainers;
import com.cerner.beadledom.lifecycle.LifecycleContainer;
import com.cerner.beadledom.lifecycle.LifecycleInjector;

/**
 * A Governator implementation of {@link LifecycleInjector}.
 *
 * <p>This should be the preferred implementation, over the default, but requires Java 8.
 *
 * <p><strong>Warning: </strong>This class is part of the internal implementation for Guice
 * lifecycle containers and should not be used directly by consumers. Instead use
 * {@link LifecycleContainer} and {@link GuiceLifecycleContainers}.
 *
 * @author John Leacox
 * @see <a href=https://github.com/Netflix/governator">Netflix Governator</a>
 * @since 1.2
 */
class GovernatorLifecycleInjector extends DelegatingInjector implements LifecycleInjector {
  private final com.netflix.governator.LifecycleInjector injector;

  private GovernatorLifecycleInjector(com.netflix.governator.LifecycleInjector injector) {
    super(injector);

    this.injector = injector;
  }

  /**
   * Creates a new instance of {@link GovernatorLifecycleInjector}.
   *
   * @param injector the Guice injector to delegate to
   */
  static GovernatorLifecycleInjector create(com.netflix.governator.LifecycleInjector injector) {
    return new GovernatorLifecycleInjector(injector);
  }

  @Override
  public void shutdown() {
    injector.shutdown();
  }
}
