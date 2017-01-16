package com.cerner.beadledom.lifecycle;

import java.util.List;

/**
 * A marker interface for classes that are functioning as a Guice (or other DI) lifecycle container.
 *
 * <p>Implementing this interface allows usage of
 * {@link GuiceLifecycleContainers#initialize(LifecycleContainer, List)} for creating a
 * lifecycle based injector, injecting the container members and starting the lifecycle.
 *
 * @author John Leacox
 * @since 1.2
 */
public interface LifecycleContainer {
}
