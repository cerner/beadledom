package com.cerner.beadledom.client;

import com.cerner.beadledom.guice.dynamicbindings.DynamicBindingProvider;

import com.google.common.base.Optional;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;
import javax.ws.rs.core.Feature;

/**
 * A Guice provider for {@link BeadledomClientBuilder}.
 *
 * @author John Leacox
 * @author Sundeep Paruvu
 * @author Nimesh Subramanian
 * @since 2.0
 */
class BeadledomClientBuilderProvider implements Provider<BeadledomClientBuilder> {
  private final Class<? extends Annotation> clientBindingAnnotation;
  private DynamicBindingProvider<BeadledomClientBuilderFactory> clientBuilderFactoryProvider;
  private DynamicBindingProvider<Optional<BeadledomClientConfiguration>> beadledomConfigProvider;

  @SuppressWarnings("unchecked")
  private final List<Class<?>> excludedBindings = Arrays.asList(
      BeadledomClient.class,
      BeadledomClientBuilder.class,
      BeadledomClientLifecycleHook.class);

  private Injector injector;
  private String correlationIdHeader;

  BeadledomClientBuilderProvider(Class<? extends Annotation> clientBindingAnnotation) {
    this.clientBindingAnnotation = clientBindingAnnotation;
  }

  @Inject
  void init(
      Injector injector,
      @CorrelationIdClientHeader String correlationIdHeaderOpt,
      DynamicBindingProvider<Optional<BeadledomClientConfiguration>> beadledomConfigProvider,
      DynamicBindingProvider<BeadledomClientBuilderFactory> clientBuilderFactoryProvider) {
    this.injector = injector;
    this.correlationIdHeader = correlationIdHeaderOpt;
    this.beadledomConfigProvider = beadledomConfigProvider;
    this.clientBuilderFactoryProvider = clientBuilderFactoryProvider;
  }

  @Override
  public BeadledomClientBuilder get() {
    Optional<BeadledomClientConfiguration> beadledomClientConfigOpt = beadledomConfigProvider.get(
        clientBindingAnnotation);
    BeadledomClientBuilder clientBuilder =
        clientBuilderFactoryProvider.get(clientBindingAnnotation).create();
    clientBuilder.setCorrelationIdName(correlationIdHeader);
    if (beadledomClientConfigOpt.isPresent()) {
      // When there is custom client config
      BeadledomClientConfiguration config = beadledomClientConfigOpt.get();

      if (config.correlationIdName() != null) {
        clientBuilder.setCorrelationIdName(config.correlationIdName());
      }

      clientBuilder.setConnectionPoolSize(config.connectionPoolSize());
      clientBuilder.setMaxPooledPerRouteSize(config.maxPooledPerRouteSize());
      clientBuilder.setSocketTimeout(config.socketTimeoutMillis(), TimeUnit.MILLISECONDS);
      clientBuilder.setConnectionTimeout(config.connectionTimeoutMillis(), TimeUnit.MILLISECONDS);
      clientBuilder.setTtl(config.ttlMillis(), TimeUnit.MILLISECONDS);

      if (config.sslContext() != null) {
        clientBuilder.sslContext(config.sslContext());
      }
      if (config.trustStore() != null) {
        clientBuilder.trustStore(config.trustStore());
      }
    }

    Injector tempInjector = getInjector();
    processInjector(tempInjector, clientBuilder);
    while (tempInjector.getParent() != null) {
      tempInjector = tempInjector.getParent();
      processInjector(tempInjector, clientBuilder);
    }
    return clientBuilder;
  }

  private Injector getInjector() {
    return injector;
  }

  private void processInjector(Injector injector, BeadledomClientBuilder builder) {
    for (Map.Entry<Key<?>, Binding<?>> bindingEntry : injector.getBindings().entrySet()) {
      if (isExcludedBinding(bindingEntry.getKey())) {
        continue;
      }

      Binding<?> binding = bindingEntry.getValue();
      if (isJaxrsClientProviderBinding(binding)) {
        Object object = binding.getProvider().get();
        if (isJaxrsFeatureOrProvider(object)) {
          builder.register(object);
        }
      }
    }
  }

  /**
   * This is to prevent circular dependency during binding resolution in the provider.
   * As we loop through every binding in the injector, we need to exclude
   * certain bindings as they havent been created yet.
   */
  private boolean isExcludedBinding(Key<?> key) {
    Class<?> rawType = key.getTypeLiteral().getRawType();
    for (Class<?> excludedClass : excludedBindings) {
      if (excludedClass.isAssignableFrom(rawType)) {
        return true;
      }
    }

    return false;
  }

  private boolean isJaxrsClientProviderBinding(Binding<?> binding) {
    Class<? extends Annotation> bindingAnnotation = binding.getKey().getAnnotationType();
    return bindingAnnotation != null && bindingAnnotation.equals(clientBindingAnnotation);
  }

  private boolean isJaxrsFeatureOrProvider(Object object) {
    Class<?> clazz = object.getClass();
    boolean isFeature = Feature.class.isAssignableFrom(clazz);
    boolean isProvider = clazz.isAnnotationPresent(javax.ws.rs.ext.Provider.class);
    return isFeature || isProvider;
  }
}
