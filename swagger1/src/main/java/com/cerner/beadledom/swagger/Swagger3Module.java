package com.cerner.beadledom.swagger;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;
import com.wordnik.swagger.converter.ModelConverter;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * Configures a service to serve Swagger 3 documentation.
 *
 * @author John Leacox
 * @since 3.0
 */
public class Swagger3Module extends AbstractModule {
  @Override
  protected void configure() {
    // Create empty multibinder in case no ModelConverter bindings exist
    Multibinder<ModelConverter> swaggerModelConverterBinder = Multibinder.newSetBinder(
        binder(), ModelConverter.class);

    bind(OpenApiResource.class).asEagerSingleton();

    bind(OpenApiReader.class).to(Reader.class);
    bind(OpenApiScanner.class).to(Swagger3GuiceJaxrsScanner.class);

    bind(Swagger3Lifecycle.class).asEagerSingleton();

    install(MultibindingsScanner.asModule());
  }
}
