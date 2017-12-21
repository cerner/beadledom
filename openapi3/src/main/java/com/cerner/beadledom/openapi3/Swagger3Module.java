package com.cerner.beadledom.openapi3;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;
import javax.inject.Singleton;

/**
 * Configures a service to serve Swagger 3 documentation.
 *
 * @author John Leacox
 * @since 3.0
 */
public class Swagger3Module extends AbstractModule {
  @Override
  protected void configure() {
    requireBinding(OpenAPIConfiguration.class);

    // Create empty multibinder in case no ModelConverter bindings exist
    Multibinder<ModelConverter> swaggerModelConverterBinder = Multibinder.newSetBinder(
        binder(), ModelConverter.class);

    //bind(OpenApiResource.class).asEagerSingleton();
    bind(Swagger3ApiResource.class).asEagerSingleton();

    bind(OpenApiScanner.class).to(Swagger3GuiceJaxrsScanner.class);

    bind(Swagger3Lifecycle.class).asEagerSingleton();

    install(MultibindingsScanner.asModule());
  }

  @Provides
  @Singleton
  OpenApiReader provideOpenApiReader(OpenAPIConfiguration configuration) {
    return new Reader(configuration);
  }

  @Provides
  @Singleton
  OpenApiContext provideOpenApiContext(
      OpenAPIConfiguration configuration, OpenApiScanner scanner, OpenApiReader reader) {
    return new GenericOpenApiContext<>()
        .openApiConfiguration(configuration)
        .openApiScanner(scanner)
        .openApiReader(reader);
  }
}
