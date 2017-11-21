package com.cerner.beadledom.swagger;

import com.wordnik.swagger.converter.ModelConverter;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiScanner;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author John Leacox
 */
public class Swagger3Lifecycle {
  private final SwaggerConfiguration swaggerConfiguration;
  private final OpenApiScanner openApiScanner;
  private final JaxrsApiReader jaxrsApiReader;
  private final Set<ModelConverter> modelConverters;

  @Inject
  Swagger3Lifecycle(
      SwaggerConfiguration swaggerConfiguration,
      OpenApiScanner openApiScanner,
      JaxrsApiReader jaxrsApiReader,
      Set<ModelConverter> modelConverters) {
    this.swaggerConfiguration = swaggerConfiguration;
    this.openApiScanner = openApiScanner;
    this.jaxrsApiReader = jaxrsApiReader;
    this.modelConverters = modelConverters;
  }

  @PostConstruct
  public void startup() {
    //for (ModelConverter modelConverter : modelConverters) {
    //  ModelConverters.addConverter(modelConverter, true);
    //}
    //
    //ConfigFactory.setConfig(swaggerConfiguration);
    //ScannerFactory.setScanner(openApiScanner);
    //ClassReaders.setReader(jaxrsApiReader);

    SwaggerConfiguration config = new SwaggerConfiguration()
        .openAPI(null);

    //ModelConverters.getInstance().addConverter();

    try {
      OpenApiContext context = new GenericOpenApiContext<>()
          .openApiConfiguration(config)
          .openApiScanner(openApiScanner)

          .init();
    } catch (OpenApiConfigurationException e) {
      throw new RuntimeException("Failed to initialize Open API 3");
    }
  }
}
