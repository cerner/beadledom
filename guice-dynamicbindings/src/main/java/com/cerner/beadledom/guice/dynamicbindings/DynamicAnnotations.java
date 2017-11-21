
package com.cerner.beadledom.guice.dynamicbindings;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

/**
 * Static convenience methods for creating {@link DynamicBindingProvider} instances.
 *
 * @author John Leacox
 * @see DynamicBindingProvider
 * @since 1.2
 */
class DynamicAnnotations {
  private DynamicAnnotations() {
  }

  /**
   * Binds a {@link DynamicBindingProvider} for the specified class.
   *
   * <p>The instance bound to the class can later be retrieved through
   * {@link DynamicBindingProvider#get(Class)} given the same annotation provided during binding
   * time. This method also 'requires' a binding of {@code clazz} to {@code annotation} to exist.
   *
   * <p>This method requires a binder, and must be used from a Guice module that is in the
   * configure phase.
   *
   * @param binder the Guice binder to bind with
   * @param clazz the class to create a {@link DynamicBindingProvider} for
   * @param annotation the binding annotation that must be bound with the provided class
   */
  static <T> void bindDynamicProvider(
      Binder binder, Class<T> clazz, Class<? extends Annotation> annotation) {
    binder.getProvider(Key.get(clazz, annotation));

    ParameterizedType type = Types.newParameterizedType(DynamicBindingProvider.class, clazz);
    DynamicBindingProvider<T> provider = new DynamicBindingProviderImpl<T>(TypeLiteral.get(clazz));
    @SuppressWarnings("unchecked")
    Key<DynamicBindingProvider<T>> key = (Key<DynamicBindingProvider<T>>) Key.get(type);
    binder.bind(key).toInstance(provider);
  }

  /**
   * Binds a {@link DynamicBindingProvider} for the specified key.
   *
   * <p>The instance bound to the key can later be retrieved through
   * {@link DynamicBindingProvider#get(Class)} given the same annotation provided during binding
   * time. This method also 'requires' a binding for {@code key}.
   *
   * <p>This method requires a binder, and must be used from a Guice module that is in the
   * configure phase.
   *
   * <p>Note: The annotation on the key will only be used for required binding checks by Guice.
   *
   * @param binder the Guice binder to bind with
   * @param key the key to create a {@link DynamicBindingProvider} for
   */
  static <T> void bindDynamicProvider(Binder binder, Key<?> key) {
    binder.getProvider(key);

    ParameterizedType type =
        Types.newParameterizedType(DynamicBindingProvider.class, key.getTypeLiteral().getType());
    @SuppressWarnings("unchecked")
    DynamicBindingProvider<T> provider =
        new DynamicBindingProviderImpl<T>((TypeLiteral<T>) key.getTypeLiteral());
    @SuppressWarnings("unchecked")
    Key<DynamicBindingProvider<T>> dynamicKey = (Key<DynamicBindingProvider<T>>) Key.get(type);
    binder.bind(dynamicKey).toInstance(provider);
  }
}
