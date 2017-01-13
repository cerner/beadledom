package com.cerner.beadledom.client;

import com.cerner.beadledom.guice.dynamicbindings.DynamicBindingProvider;

import com.google.inject.Inject;

import java.lang.annotation.Annotation;

import javax.inject.Provider;

/**
 * A Guice provider for {@link ResteasyClientLifecycleHook}.
 *
 * @author John Leacox
 */
class BeadledomClientLifecycleHookProvider implements Provider<BeadledomClientLifecycleHook> {
  private final Class<? extends Annotation> annotation;

  private DynamicBindingProvider<BeadledomClient> beadledomClientProvider;

  BeadledomClientLifecycleHookProvider(Class<? extends Annotation> annotation) {
    this.annotation = annotation;
  }

  @Inject
  void init(DynamicBindingProvider<BeadledomClient> beadledomClientProvider) {
    this.beadledomClientProvider = beadledomClientProvider;
  }

  @Override
  public BeadledomClientLifecycleHook get() {
    return new BeadledomClientLifecycleHook(beadledomClientProvider.get(annotation), annotation);
  }
}
