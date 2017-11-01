package com.cerner.beadledom.resteasy;

import com.cerner.beadledom.lifecycle.GuiceLifecycleContainers;
import com.cerner.beadledom.lifecycle.LifecycleContainer;
import com.cerner.beadledom.lifecycle.LifecycleInjector;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * A base context listener for Resteasy that provides lifecycle support via Governator.
 *
 * <p>When using this context listener the {@link ResteasyModule} must also be registered with guice
 * to provide some required bindings.
 *
 * @author John Leacox
 */
public abstract class ResteasyContextListener extends ResteasyBootstrap implements
    ServletContextListener, LifecycleContainer {
  private LifecycleInjector injector;

  @Override
  public void contextInitialized(ServletContextEvent event) {
    super.contextInitialized(event);

    final ServletContext context = event.getServletContext();
    final Registry registry = (Registry) context.getAttribute(Registry.class.getName());
    final ResteasyProviderFactory providerFactory =
        (ResteasyProviderFactory) context.getAttribute(ResteasyProviderFactory.class.getName());
    final ModuleProcessor processor = new ModuleProcessor(registry, providerFactory);

    final List<? extends Module> appModules = getModules(context);

    List<Module> modules = Lists.newArrayList();
    modules.addAll(appModules);

    injector = GuiceLifecycleContainers.initialize(this, modules);

    withInjector(injector);

    processor.processInjector(injector);
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    if (injector != null) {
      injector.shutdown();
    }

    super.contextDestroyed(event);
  }

  /**
   * A method that allows interaction with the Guice injector.
   *
   * <p>Defaults to a no-op. Override to use the injector.
   */
  protected void withInjector(Injector injector) {
  }

  /**
   * Returns the list of Guice modules to be used with Resteasy.
   */
  protected abstract List<? extends Module> getModules(ServletContext context);
}
