package com.cerner.beadledom.openapi3;

import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
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
  private final OpenApiContext context;
  private final Set<ModelConverter> modelConverters;

  @Inject
  Swagger3Lifecycle(
      OpenApiContext context,
      Set<ModelConverter> modelConverters) {
    this.context = context;
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
      context.init();
    } catch (OpenApiConfigurationException e) {
      throw new RuntimeException("Failed to initialize Open API 3");
    }
  }
}
