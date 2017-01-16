package com.cerner.beadledom.lifecycle;

import com.google.inject.Module;
import java.util.List;

/**
 * Static utility methods pertaining to Guice lifecycle containers.
 *
 * @author John Leacox
 * @since 1.2
 */
public final class GuiceLifecycleContainers {
  private GuiceLifecycleContainers() {
  }

  /**
   * Creates a {@link LifecycleInjector} and injects members on the lifecycle container.
   *
   * <p>This method is useful for initializing a lifecycle container and getting a
   * {@link LifecycleInjector} that can be used at the end of the containers lifecycle for shutting
   * down components by calling {@link LifecycleInjector#shutdown()}.
   *
   * <p>In addition to the {@code modules} provided to this method, additional modules will also be
   * installed to configure lifecycle management. After this method executes, all injectable fields
   * and methods on the {@code container} will be present, unless nullable, and all eager singletons
   * will be created and initialized.
   *
   * @param <C> the type of the container
   * @param container the container that will manage the lifecycle
   * @param modules the Guice modules to use during the lifecycle of the container
   * @return a {@link LifecycleInjector} using the provided {@code modules} and additional lifecycle
   *     management modules
   */
  public static <C extends LifecycleContainer> LifecycleInjector initialize(
      C container, List<Module> modules) {
    if (container == null) {
      throw new NullPointerException("container:null");
    } else if (modules == null) {
      throw new NullPointerException("modules:null");
    }

    LifecycleInjector injector = LifecycleInjectorBuilder.fromModules(modules).createInjector();
    injector.injectMembers(container);

    return injector;
  }
}
