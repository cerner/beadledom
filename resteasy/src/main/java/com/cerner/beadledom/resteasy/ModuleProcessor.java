package com.cerner.beadledom.resteasy;

import com.google.inject.Binding;
import com.google.inject.Injector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.guice.i18n.LogMessages;
import org.jboss.resteasy.plugins.guice.i18n.Messages;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

/**
 * Processes a given life cycle injector that contains Guice modules.
 *
 * <p>This class expands upon {@link org.jboss.resteasy.plugins.guice.ModuleProcessor} by enforcing
 * restrictions on available JAX-RS {@link Provider}s to be added to {@link ResteasyProviderFactory}.
 *
 * @author Charan Panchagnula
 */
public class ModuleProcessor {

  private final Registry registry;
  private final ResteasyProviderFactory providerFactory;

  /**
   * Constructor for ModuleProcessor.
   *
   * @param registry {@link Registry} for adding resource implementation endpoints
   * @param providerFactory {@link ResteasyProviderFactory} used for registering JAX-RS {@link Provider} instances
   */
  public ModuleProcessor(final Registry registry, final ResteasyProviderFactory providerFactory) {
    Objects.requireNonNull(registry, "registry: null");
    Objects.requireNonNull(providerFactory, "providerFactory: null");

    this.registry = registry;
    this.providerFactory = providerFactory;
  }

  /**
   * Processes the injector by registering bindings for dependencies associated to each type.
   *
   * @param injector The {@link Injector} instance to be processed
   */
  public void processInjector(final Injector injector) {
    Objects.requireNonNull(injector, "injector: null");

    final List<Binding<?>> rootResourceBindings = new ArrayList<>();
    for (final Binding<?> binding : injector.getBindings().values()) {
      final Type type = binding.getKey().getTypeLiteral().getRawType();
      final Class<?> beanClass = (Class) type;
      if (GetRestful.isRootResource(beanClass)) {
        // deferred registration
        rootResourceBindings.add(binding);
      }
      if (beanClass.isAnnotationPresent(Provider.class)) {
        // For a single running resteasy instance, there is no reason to register annotated bindings
        // because it can potentially lead to multiple providers of the same type bound within the injector.
        if (binding.getKey().getAnnotationType() == null) {
          LogMessages.LOGGER.info(Messages.MESSAGES.registeringProviderInstance(beanClass.getName()));
          providerFactory.registerProviderInstance(binding.getProvider().get());
        }
      }
    }
    for (final Binding<?> binding : rootResourceBindings) {
      final Class<?> beanClass = (Class) binding.getKey().getTypeLiteral().getType();
      final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);

      LogMessages.LOGGER.info(Messages.MESSAGES.registeringFactory(beanClass.getName()));
      registry.addResourceFactory(resourceFactory);
    }
  }
}
