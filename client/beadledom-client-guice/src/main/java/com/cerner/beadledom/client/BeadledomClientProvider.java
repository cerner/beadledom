package com.cerner.beadledom.client;

import com.cerner.beadledom.guice.dynamicbindings.DynamicBindingProvider;

import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * A Guice provider for {@link BeadledomClient}.
 *
 * @author Sundeep Paruvu
 * @since 2.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
class BeadledomClientProvider implements Provider<BeadledomClient> {
  private final Class<? extends Annotation> clientBindingAnnotation;
  private DynamicBindingProvider<BeadledomClientBuilder> clientBuilderProvider;

  BeadledomClientProvider(Class<? extends Annotation> clientBindingAnnotation) {
    this.clientBindingAnnotation = clientBindingAnnotation;
  }

  @Inject
  public void init(DynamicBindingProvider<BeadledomClientBuilder> clientBuilder) {
    this.clientBuilderProvider = clientBuilder;
  }

  @Override
  public BeadledomClient get() {
    return clientBuilderProvider.get(clientBindingAnnotation).build();
  }
}
