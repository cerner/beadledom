package com.cerner.beadledom.lifecycle.legacy;

import com.cerner.beadledom.lifecycle.GuiceLifecycleContainers;
import com.cerner.beadledom.lifecycle.LifecycleContainer;

/**
 * A holder for lifecycle methods annotated with JSR-250 lifecycle annotations.
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
public interface InvokableLifecycleMethod {
  /**
   * Invokes the held lifecycle method using reflection.
   */
  void invoke();
}
