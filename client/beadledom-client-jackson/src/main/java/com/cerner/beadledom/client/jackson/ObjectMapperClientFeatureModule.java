package com.cerner.beadledom.client.jackson;

import com.cerner.beadledom.guice.BindingAnnotations;
import com.cerner.beadledom.jackson.JacksonModule;
import com.google.inject.AbstractModule;
import java.lang.annotation.Annotation;

/**
 * A guice modules for a default ObjectMapper for beadledom clients.
 *
 * <p>To use this module install it using the {@link ObjectMapperClientFeatureModule#with(Class)}
 * method providing your own client binding annotation. This binding annotation is needed to
 * namespace this feature to the client matching the binding annotation.
 *
 * <p>Installs:
 * <ul>
 *     <li>{@link JacksonModule}</li>
 *     <li>{@link AnnotatedJacksonModule}</li>
 * </ul>
 *
 * @author John Leacox
 * @since 1.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public class ObjectMapperClientFeatureModule extends AbstractModule {
  private final Class<? extends Annotation> clientBindingAnnotation;

  private ObjectMapperClientFeatureModule(Class<? extends Annotation> clientBindingAnnotation) {
    this.clientBindingAnnotation = clientBindingAnnotation;
  }

  /**
   * Creates a new instance of {@code ObjectMapperClientFeatureModule} for the specified binding
   * annotation.
   */
  public static ObjectMapperClientFeatureModule with(
      Class<? extends Annotation> clientBindingAnnotation) {
    BindingAnnotations.checkIsBindingAnnotation(clientBindingAnnotation);

    return new ObjectMapperClientFeatureModule(clientBindingAnnotation);
  }

  @Override
  protected void configure() {
    install(AnnotatedJacksonModule.with(clientBindingAnnotation));
  }
}
