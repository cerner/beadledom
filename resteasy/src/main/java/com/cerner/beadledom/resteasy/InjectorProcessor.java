package com.cerner.beadledom.resteasy;

import com.google.inject.Binding;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor for registering JAX-RS providers and root resources from bindings tied to a Guice injector.
 *
 * @author Charan Panchagnula
 * @since 3.3
 */
class InjectorProcessor {
  private static final Logger logger = LoggerFactory.getLogger(InjectorProcessor.class);

  private final Registry registry;
  private final ResteasyProviderFactory providerFactory;

  /**
   * Creates a new instance for InjectorProcessor with the given Resteasy resource registry
   * and provider factory that the processor will register the Guice bindings with.
   *
   * @param registry Resteasy resource {@link Registry}
   * @param providerFactory The {@link ResteasyProviderFactory} used for registering Guice bindings.
   */
  public InjectorProcessor(final Registry registry, final ResteasyProviderFactory providerFactory) {
    Objects.requireNonNull(registry, "registry: null");
    Objects.requireNonNull(providerFactory, "providerFactory: null");

    this.registry = registry;
    this.providerFactory = providerFactory;
  }

  /**
   * Processes all bindings in the Guice injector and registers all non-annotated JAX-RS providers
   * and root resources with Resteasy.
   *
   * <p>Annotated bindings of JAX-RS providers and root resources are ignored, as these are most
   * likely related to HTTP client bindings. A service should only have one set of JAX-RS service
   * bindings, so they should be bound in Guice without an annotation.</p>
   *
   * @param injector The {@link Injector} instance to be processed
   */
  public void process(final Injector injector) {
    Objects.requireNonNull(injector, "injector: null");

    Predicate<Binding<?>> isNonAnnotatedBinding = binding -> binding.getKey().getAnnotationType() == null;
    List<Binding<?>> nonAnnotatedBindings = injector.getBindings().values().stream().filter(isNonAnnotatedBinding)
        .collect(Collectors.toList());

    List<Binding<?>> providerBindings = new ArrayList<>();
    List<Binding<?>> rootResourceBindings = new ArrayList<>();

    for (Binding<?> binding : nonAnnotatedBindings) {
      Class<?> bindingClass = binding.getKey().getTypeLiteral().getRawType();
      if (bindingClass.isAnnotationPresent(Provider.class)) {
        providerBindings.add(binding);
      }
      if (GetRestful.isRootResource(bindingClass)) {
        rootResourceBindings.add(binding);
      }
    }

    registerProviders(providerBindings);
    registerRootResources(rootResourceBindings);
  }

  private void registerProviders(List<Binding<?>> providerBindings) {
    for (Binding<?> binding : providerBindings) {
      Class<?> bindingClass = binding.getKey().getTypeLiteral().getRawType();
      providerFactory.registerProviderInstance(binding.getProvider().get());

      logger.info("registering provider instance for {}", bindingClass.getName());
    }
  }

  private void registerRootResources(List<Binding<?>> rootResourceBindings) {
    for (Binding<?> binding : rootResourceBindings) {
      Class<?> bindingClass = binding.getKey().getTypeLiteral().getRawType();
      ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), bindingClass);
      registry.addResourceFactory(resourceFactory);

      logger.info("registering factory for {}", bindingClass.getName());
    }
  }
}
