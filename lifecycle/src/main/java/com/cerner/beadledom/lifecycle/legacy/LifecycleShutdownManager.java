package com.cerner.beadledom.lifecycle.legacy;

import com.cerner.beadledom.lifecycle.GuiceLifecycleContainers;
import com.cerner.beadledom.lifecycle.LifecycleContainer;
import java.util.List;
import javax.annotation.PreDestroy;

/**
 * A manager of lifecycle shutdown hooks.
 *
 * <p>Lifecycle methods annotated with {@literal @}{@link PreDestroy} are registered with this
 * manager, and when {@link #shutdown()} is executed all lifecycle shutdown methods are executed.
 *
 * <p><strong>Warning: </strong>This class is part of the internal implementation for Guice
 * lifecycle containers and should not be used directly by consumers. Instead use
 * {@link LifecycleContainer} and {@link GuiceLifecycleContainers}.
 *
 * @author John Leacox
 * @since 1.2
 * @deprecated The Governator lifecycle implementation should be used instead if possible
 */
@Deprecated
public interface LifecycleShutdownManager {
  /**
   * Registers lifecycle shutdown methods to be executed when {@link #shutdown()}) is executed.
   */
  void addPreDestroyMethods(List<InvokableLifecycleMethod> preDestroyMethods);

  /**
   * Executes all registered lifecycle shutdown methods.
   */
  void shutdown();
}
