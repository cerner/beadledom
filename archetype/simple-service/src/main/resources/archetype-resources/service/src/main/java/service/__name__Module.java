#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import ${package}.service.resource.HelloWorldResourceImpl;
import ${package}.api.HelloWorldResource;
import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.cerner.beadledom.resteasy.ResteasyModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.core.SwaggerSpec;

public class ${name}Module extends AbstractModule {

  protected void configure() {
    install(new ResteasyModule());

    BuildInfo buildInfo = BuildInfo.load(getClass().getResourceAsStream("build-info.properties"));
    bind(BuildInfo.class).toInstance(buildInfo);
    bind(ServiceMetadata.class).toInstance(ServiceMetadata.create(buildInfo));

    bind(HelloWorldResource.class).to(HelloWorldResourceImpl.class);
  }

  @Provides
  SwaggerConfig provideSwaggerConfig(ServiceMetadata serviceMetadata) {
    SwaggerConfig config = new SwaggerConfig();
    config.setApiVersion(serviceMetadata.getBuildInfo().getVersion());
    config.setSwaggerVersion(SwaggerSpec.version());
    return config;
  }
}
