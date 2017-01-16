package com.cerner.beadledom.lifecycle.legacy;

import com.google.inject.AbstractModule;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A Guice module that hooks into the JVM shutdown and calls shutdown on the
 * {@link LifecycleShutdownManager} when the JVM shuts down.
 *
 * @author John Leacox
 * @since 1.2
 * @deprecated The Governator lifecycle implementation should be used instead if possible
 */
@Deprecated
final class ShutdownHookModule extends AbstractModule {
  @Singleton
  static class SystemShutdownHook extends Thread {
    @Inject
    public SystemShutdownHook(final LifecycleShutdownManager shutdownManager) {
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          shutdownManager.shutdown();
        }
      });
    }
  }

  @Override
  protected void configure() {
    requireBinding(LifecycleShutdownManager.class);

    bind(SystemShutdownHook.class).asEagerSingleton();
  }
}
