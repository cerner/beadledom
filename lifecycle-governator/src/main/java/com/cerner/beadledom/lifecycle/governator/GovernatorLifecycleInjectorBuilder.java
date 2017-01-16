package com.cerner.beadledom.lifecycle.governator;

import com.cerner.beadledom.lifecycle.GuiceLifecycleContainers;
import com.cerner.beadledom.lifecycle.LifecycleContainer;
import com.cerner.beadledom.lifecycle.LifecycleInjector;
import com.cerner.beadledom.lifecycle.LifecycleInjectorBuilder;
import com.google.inject.Module;
import com.netflix.governator.InjectorBuilder;
import com.netflix.governator.LifecycleModule;
import com.netflix.governator.ShutdownHookModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A governator implementation of {@link LifecycleInjectorBuilder}.
 *
 * <p><strong>Warning: </strong>This class is part of the internal implementation for Guice
 * lifecycle containers and should not be used directly by consumers. Instead use
 * {@link LifecycleContainer} and {@link GuiceLifecycleContainers}.
 *
 * @author John Leacox
 * @since 1.2
 */
public class GovernatorLifecycleInjectorBuilder extends LifecycleInjectorBuilder {
  private List<Module> modules = Collections.emptyList();

  public GovernatorLifecycleInjectorBuilder() {
  }

  @Override
  public GovernatorLifecycleInjectorBuilder modules(List<Module> modules) {
    if (modules == null) {
      throw new NullPointerException("modules:null");
    }

    this.modules = modules;
    return this;
  }

  @Override
  public LifecycleInjector createInjector() {
    List<Module> allModules = new ArrayList<Module>();
    allModules.addAll(modules);
    allModules.add(new ShutdownHookModule());
    allModules.add(new LifecycleModule());

    return GovernatorLifecycleInjector.create(
        InjectorBuilder.fromModules(allModules).createInjector());
  }
}
