package com.cerner.beadledom.swagger;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.converter.ModelConverter;
import com.wordnik.swagger.converter.ModelConverters;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import com.wordnik.swagger.jaxrs.config.JaxrsScanner;
import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Configures a service to serve Swagger documentation at '/api-docs', and the Swagger UI at
 * '/meta/swagger/ui'.
 *
 * <p>To use this, you must provide a SwaggerConfig such as the following:
 *
 * <p><pre>{@code @Provides
 * SwaggerConfig provideSwaggerConfig(ServiceMetadata serviceMetadata) {
 *   SwaggerConfig config = new SwaggerConfig();
 *   config.setApiInfo(new ApiInfo(
 *       "Name of My Service",
 *       "A description of my service. My service lets you do some things. It's owned "
 *       + " by My Awesome Team",
 *       null, null, null, null));
 *   config.setApiVersion(serviceMetadata.getBuildInfo().getVersion());
 *   config.setSwaggerVersion(SwaggerSpec.version());
 *   return config;
 * }
 * }</pre>
 *
 * <p>Then annotate your resources, operations, and models with the Swagger annotations.
 *
 * <p>Provides the following JAX-RS resources and providers:
 * <ul>
 *     <li>{@link com.cerner.beadledom.swagger.SwaggerApiResource}</li>
 *     <li>{@link com.cerner.beadledom.swagger.SwaggerUiResource}</li>
 *     <li>{@link com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider}</li>
 *     <li>{@link com.wordnik.swagger.jaxrs.listing.ResourceListingProvider}</li>
 * </ul>
 *
 * <p>Requires:
 * <ul>
 *     <li>{@link com.wordnik.swagger.config.SwaggerConfig}</li>
 * </ul>
 *
 * <p>You may also supply set bindings for {@link com.wordnik.swagger.converter.ModelConverter}.
 * These will be added to the list of model converters (before the default converter, but otherwise
 * in unspecified order).
 *
 * <p>Bindings for the following are provided and used internally:
 * <ul>
 *     <li>{@link com.wordnik.swagger.jaxrs.JaxrsApiReader}</li>
 *     <li>{@link com.wordnik.swagger.jaxrs.config.JaxrsScanner}</li>
 * </ul>
 */
public class SwaggerModule extends AbstractModule {
  @Override
  protected void configure() {
    requireBinding(SwaggerConfig.class);

    // Create empty multibinder in case no ModelConverter bindings exist
    Multibinder.newSetBinder(binder(), ModelConverter.class);

    bind(SwaggerApiResource.class);
    bind(SwaggerUiResource.class);

    bind(ApiDeclarationProvider.class);
    bind(ResourceListingProvider.class);

    bind(JaxrsApiReader.class).to(DefaultJaxrsApiReader.class);
    bind(JaxrsScanner.class).to(SwaggerGuiceJaxrsScanner.class);

    bind(SwaggerLifecycleHook.class).asEagerSingleton();
  }

  static class SwaggerLifecycleHook {
    private final SwaggerConfig swaggerConfig;
    private final JaxrsScanner jaxrsScanner;
    private final JaxrsApiReader jaxrsApiReader;
    private final Set<ModelConverter> modelConverters;

    @Inject
    SwaggerLifecycleHook(
        SwaggerConfig swaggerConfig,
        JaxrsScanner jaxrsScanner,
        JaxrsApiReader jaxrsApiReader,
        Set<ModelConverter> modelConverters) {
      this.swaggerConfig = swaggerConfig;
      this.jaxrsScanner = jaxrsScanner;
      this.jaxrsApiReader = jaxrsApiReader;
      this.modelConverters = modelConverters;
    }

    @PostConstruct
    public void startup() {
      for (ModelConverter modelConverter : modelConverters) {
        ModelConverters.addConverter(modelConverter, true);
      }

      ConfigFactory.setConfig(swaggerConfig);
      ScannerFactory.setScanner(jaxrsScanner);
      ClassReaders.setReader(jaxrsApiReader);
    }
  }
}
