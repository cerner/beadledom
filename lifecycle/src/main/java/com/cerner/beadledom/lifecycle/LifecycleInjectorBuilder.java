package com.cerner.beadledom.lifecycle;

import com.cerner.beadledom.lifecycle.legacy.BeadledomLifecycleInjectorBuilder;
import com.google.inject.Module;
import java.util.List;
import java.util.ServiceLoader;

/**
 * An abstract builder for creating an instance of {@link LifecycleInjectorBuilder}.
 *
 * <p>By default this class will look for the {@code GovernatorLifecycleInjectorBuilder} on the
 * classpath and attempt to use that; if that is not found it will fallback to
 * {@link BeadledomLifecycleInjectorBuilder}. The Governator implementation should be preferred, but
 * this fallback behavior is required to support both Java 6 and Java 8 until Java 6 support is
 * dropped.
 *
 * <p><strong>Warning: </strong>This class is part of the internal implementation for Guice
 * lifecycle containers and should not be used directly by consumers. Instead use
 * {@link LifecycleContainer} and {@link GuiceLifecycleContainers}.
 *
 * @author John Leacox
 * @since 1.2
 */
public abstract class LifecycleInjectorBuilder {
  /**
   * Creates a new instance of {@link LifecycleInjectorBuilder} with the specified modules.
   *
   * @param modules the Guice modules to use for the injector builder.
   */
  public static LifecycleInjectorBuilder fromModules(List<Module> modules) {
    ServiceLoader<LifecycleInjectorBuilder> builderLoader =
        ServiceLoader.load(LifecycleInjectorBuilder.class);

    // Default to the governator implementation if present
    for (LifecycleInjectorBuilder builder : builderLoader) {
      if (builder.getClass().getName()
          .equals("com.cerner.beadledom.lifecycle.governator.GovernatorLifecycleInjectorBuilder")) {
        return builder.modules(modules);
      }
    }

    // If the governator service provider is not found, default to beadledom Java 6 implementation.
    return new BeadledomLifecycleInjectorBuilder().modules(modules);
  }

  /**
   * Set or override the list of modules for the builder.
   *
   * @param modules the Guice modules to use for the injector builder.
   * @return this builder
   */
  public abstract LifecycleInjectorBuilder modules(List<Module> modules);

  /**
   * Creates a new {@link LifecycleInjector} with the builder modules.
   */
  public abstract LifecycleInjector createInjector();
}
