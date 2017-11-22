package com.cerner.beadledom.swagger;

import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Bootstraps swagger 3.
 *
 * @author John Leacox
 * @since 3.0
 */
public class Swagger3Lifecycle {
  private final SwaggerConfiguration swaggerConfiguration;
  private final OpenApiScanner openApiScanner;
  private final OpenApiReader openApiReader;
  private final Set<ModelConverter> modelConverters;

  @Inject
  Swagger3Lifecycle(
      SwaggerConfiguration swaggerConfiguration,
      OpenApiScanner openApiScanner,
      OpenApiReader openApiReader,
      Set<ModelConverter> modelConverters) {
    this.swaggerConfiguration = swaggerConfiguration;
    this.openApiScanner = openApiScanner;
    this.openApiReader = openApiReader;
    this.modelConverters = modelConverters;
  }

  @PostConstruct
  void startup() {
    //OpenAPI oas = new OpenAPI();
    //Info info = new Info()
    //    .title("Swagger Sample App")
    //    .description(
    //        "This is a sample server Petstore server.  You can find out more about Swagger "
    //            + "at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).  For this sample, "
    //            + "you can use the api key `special-key` to test the authorization filters.")
    //    .termsOfService("http://swagger.io/terms/")
    //    .contact(new Contact()
    //        .email("apiteam@swagger.io"))
    //    .license(new License()
    //        .name("Apache 2.0")
    //        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));

    for (ModelConverter modelConverter : modelConverters) {
      ModelConverters.getInstance().addConverter(modelConverter);
    }

    try {
      new GenericOpenApiContext<>()
          .openApiConfiguration(swaggerConfiguration)
          .openApiScanner(openApiScanner)
          .openApiReader(openApiReader)
          .init();
    } catch (OpenApiConfigurationException e) {
      throw new RuntimeException("Failed to initialize Open API 3");
    }
  }
}
