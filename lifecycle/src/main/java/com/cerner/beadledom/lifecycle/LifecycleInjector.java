package com.cerner.beadledom.lifecycle;

import com.google.inject.Injector;
import javax.annotation.PreDestroy;

/**
 * A wrapper of a Guice injector that adds a shutdown method for executing registered shutdown
 * ({@literal @}{@link PreDestroy}) lifecycle methods.
 *
 * <p><strong>Warning: </strong>This class is part of the internal implementation for Guice
 * lifecycle containers and should not be used directly by consumers. Instead use
 * {@link LifecycleContainer} and {@link GuiceLifecycleContainers}.
 *
 * @author John Leacox
 * @since 1.2
 */
public interface LifecycleInjector extends Injector {
  /**
   * Shutdown the underlying lifecycle management implementation, which will invoke all lifecycle
   * shutdown hooks.
   */
  void shutdown();
}
