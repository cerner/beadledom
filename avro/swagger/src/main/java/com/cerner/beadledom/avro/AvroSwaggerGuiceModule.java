package com.cerner.beadledom.avro;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;
import io.swagger.converter.ModelConverter;

/**
 * A Guice module that provides Jackson Avro serialization support for Swagger.
 *
 * <p>Provides:
 * <ul>
 *     <li>Avro serialization support for Swagger via {@link Multibinder Multibinder&lt;ModelConverter&gt;}</li>
 * </ul>
 *
 * <p>Installs:
 * <ul>
 *   <li> {@link MultibindingsScanner} </li>
 * </ul>
 *
 * @author John Leacox
 * @since 1.0
 */
public class AvroSwaggerGuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<ModelConverter> swaggerModelConverterBinder = Multibinder.newSetBinder(binder(),
        ModelConverter.class);

    swaggerModelConverterBinder.addBinding().to(SwaggerAvroModelConverter.class);

    install(MultibindingsScanner.asModule());
  }
}
