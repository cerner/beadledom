package com.cerner.beadledom.guice.dynamicbindings;

import com.cerner.beadledom.guice.BindingAnnotations;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;

/**
 * A private module associated with a specific binding annotation.
 *
 * <p>This module allows binding of {@link DynamicBindingProvider} instances for a given type and
 * this modules binding annotation. This is useful for namespacing bindings for library consumers
 * that may be combined within an application and must avoid duplicate bindings. For instance this
 * is useful with beadledom-client so that each service/http client library can have its own
 * namespaced BeadledomClient and feature bindings.
 *
 * <p><strong>Note: </strong>This module is a private module, so any bindings that should be
 * available to consumers must be exposed via the {@link #expose} methods. Also, as noted on the
 * {@link DynamicBindingProvider} documentation, it is rarely a good idea to expose the dynamic
 * binding provider itself; instead exposing a downstream binding that may use the dynamic binding
 * provider internally.
 *
 * @author John Leacox
 * @see PrivateModule
 * @since 1.2
 */
public abstract class AnnotatedModule extends PrivateModule {
  private final Class<? extends Annotation> bindingAnnotation;

  protected AnnotatedModule(Class<? extends Annotation> bindingAnnotation) {
    BindingAnnotations.checkIsBindingAnnotation(bindingAnnotation);

    this.bindingAnnotation = bindingAnnotation;
  }

  /**
   * Returns the binding annotation associated with this module.
   */
  protected Class<? extends Annotation> getBindingAnnotation() {
    return bindingAnnotation;
  }

  /**
   * Binds a {@link DynamicBindingProvider} for the given class and this module's binding
   * annotation.
   */
  protected <T> void bindDynamicProvider(Class<T> clazz) {
    DynamicAnnotations.bindDynamicProvider(binder(), clazz, getBindingAnnotation());
  }

  /**
   * Binds a {@link DynamicBindingProvider} for the given {@link TypeLiteral} and this module's
   * binding annotation.
   */
  protected <T> void bindDynamicProvider(TypeLiteral<T> typeLiteral) {
    DynamicAnnotations.bindDynamicProvider(binder(), Key.get(typeLiteral, getBindingAnnotation()));
  }
}
