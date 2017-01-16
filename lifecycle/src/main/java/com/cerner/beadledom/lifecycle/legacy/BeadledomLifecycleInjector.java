package com.cerner.beadledom.lifecycle.legacy;

import com.cerner.beadledom.lifecycle.DelegatingInjector;
import com.cerner.beadledom.lifecycle.LifecycleInjector;
import com.google.inject.Injector;

/**
 * A legacy implementation of {@link LifecycleInjector} providing support for Java 6.
 *
 * @author John Leacox
 * @since 1.2
 * @deprecated The Governator lifecycle implementation should be used instead if possible
 */
@Deprecated
class BeadledomLifecycleInjector extends DelegatingInjector implements LifecycleInjector {
  private final LifecycleShutdownManager shutdownManager;

  private BeadledomLifecycleInjector(Injector injector, LifecycleShutdownManager shutdownManager) {
    super(injector);

    this.shutdownManager = shutdownManager;
  }

  /**
   * Creates a new instance of {@link BeadledomLifecycleInjector}.
   *
   * @param injector the Guice injector to delegate to
   * @param shutdownManager the shutdownManager to be used when shutting down the injector at the
   *     end of a container lifecycle
   */
  static BeadledomLifecycleInjector create(
      Injector injector, LifecycleShutdownManager shutdownManager) {
    return new BeadledomLifecycleInjector(injector, shutdownManager);
  }

  @Override
  public void shutdown() {
    shutdownManager.shutdown();
  }
}
