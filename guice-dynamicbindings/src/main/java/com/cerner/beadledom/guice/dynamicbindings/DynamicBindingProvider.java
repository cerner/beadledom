package com.cerner.beadledom.guice.dynamicbindings;

import com.google.inject.PrivateModule;
import java.lang.annotation.Annotation;

/**
 * A special Guice provider that can get an instance of a binding for a dynamically determined
 * binding annotation.
 *
 * <p>This class allows providers and modules to be assigned a binding annotation at runtime,
 * rather than compile time, and then use that binding annotation to retrieve bindings for that
 * annotation.
 *
 * <p><strong>Warning: </strong>It is rarely a good idea to expose instances of this provider
 * globally. Ideally these should be used within a {@link PrivateModule} or {@link AnnotatedModule}
 * and kept within the scope of those modules. There is still benefit from using a dynamic binding
 * provider in this way and then exposing downstream types that can be built from the dynamic
 * binding provider.
 *
 * @author John Leacox
 * @since 1.2
 */
public interface DynamicBindingProvider<T> {
  /**
   * Returns an instance of {@code T} bound to the specified annotation.
   *
   * @throws RuntimeException if the injector encounters an error while providing an instance or
   *     an instance is not bound.
   */
  T get(Class<? extends Annotation> bindingAnnotation);
}
