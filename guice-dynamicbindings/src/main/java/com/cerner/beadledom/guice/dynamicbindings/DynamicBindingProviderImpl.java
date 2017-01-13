package com.cerner.beadledom.guice.dynamicbindings;

import com.cerner.beadledom.guice.BindingAnnotations;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;

/**
 * Internal implementation of {@link DynamicBindingProvider}.
 *
 * @author John Leacox
 * @since 1.2
 */
class DynamicBindingProviderImpl<T> implements DynamicBindingProvider<T> {
  private Injector injector;
  private TypeLiteral<T> type;

  DynamicBindingProviderImpl(TypeLiteral<T> type) {
    this.type = type;
  }

  @Inject
  void init(Injector injector) {
    this.injector = injector;
  }

  @Override
  public synchronized T get(Class<? extends Annotation> bindingAnnotation) {
    BindingAnnotations.checkIsBindingAnnotation(bindingAnnotation);

    Key<T> key = Key.get(type, bindingAnnotation);
    return injector.getInstance(key);
  }
}
