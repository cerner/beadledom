package com.cerner.beadledom.lifecycle.legacy;

import com.cerner.beadledom.lifecycle.GuiceLifecycleContainers;
import com.cerner.beadledom.lifecycle.LifecycleContainer;
import com.cerner.beadledom.lifecycle.LifecycleInjector;
import com.cerner.beadledom.lifecycle.LifecycleInjectorBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A legacy implementation of {@link LifecycleInjectorBuilder} that supports Java 6.
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
public class BeadledomLifecycleInjectorBuilder extends LifecycleInjectorBuilder {
  private List<Module> modules = Collections.emptyList();

  public BeadledomLifecycleInjectorBuilder() {
  }

  @Override
  public LifecycleInjectorBuilder modules(List<Module> modules) {
    if (modules == null) {
      throw new NullPointerException("modules:null");
    }

    this.modules = modules;
    return this;
  }

  @Override
  public LifecycleInjector createInjector() {
    final LifecycleShutdownManager shutdownManager = new LifecycleShutdownManagerImpl();

    List<Module> fullModules = new ArrayList<Module>();
    fullModules.addAll(modules);
    fullModules.add(new BeadledomLifecycleModule());
    fullModules.add(new ShutdownHookModule());
    fullModules.add(new AbstractModule() {
      @Override
      protected void configure() {
        bind(LifecycleShutdownManager.class).toInstance(shutdownManager);
        requestInjection(BeadledomLifecycleInjectorBuilder.this);
      }
    });

    Injector injector = Guice.createInjector(fullModules);
    return BeadledomLifecycleInjector.create(injector, shutdownManager);
  }
}
