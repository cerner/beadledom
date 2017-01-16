package com.cerner.beadledom.guice;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Annotation;
import javax.inject.Qualifier;

/**
 * Static convenience methods for working with and validating Guice binding annotations.
 *
 * <p>Supported binding annotations are {@link BindingAnnotation} and {@link Qualifier}
 *
 * @author John Leacox
 * @see BindingAnnotation
 * @see Qualifier
 * @since 1.2
 */
public final class BindingAnnotations {
  private BindingAnnotations() {
  }

  /**
   * Returns true if {@code annotation} is a Guice binding annotation; false otherwise.
   */
  public static boolean isBindingAnnotation(Class<? extends Annotation> annotation) {
    return annotation.isAnnotationPresent(BindingAnnotation.class)
        || annotation.isAnnotationPresent(Qualifier.class);
  }

  /**
   * Returns {@code annotation} if it is a Guice binding annotation.
   *
   * @throws IllegalArgumentException if {@code annotation} is not a Guice binding annotation
   */
  public static Class<? extends Annotation> checkIsBindingAnnotation(
      Class<? extends Annotation> annotation) {
    if (!isBindingAnnotation(annotation)) {
      throw new IllegalArgumentException(
          "Must be a binding annotation (BindingAnnotation or Qualifier)");
    }
    return annotation;
  }
}
