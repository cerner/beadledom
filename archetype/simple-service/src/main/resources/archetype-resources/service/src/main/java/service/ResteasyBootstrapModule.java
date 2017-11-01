#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import com.cerner.beadledom.health.HealthModule;
import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.cerner.beadledom.resteasy.ResteasyModule;
import com.cerner.beadledom.stagemonitor.StagemonitorModule;
import com.cerner.beadledom.swagger.SwaggerModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.core.SwaggerSpec;

public class ResteasyBootstrapModule extends AbstractModule {

  protected void configure() {
    install(new ResteasyModule());
    install(new StagemonitorModule());
    install(new SwaggerModule());
    install(new HealthModule());

    BuildInfo buildInfo = BuildInfo.load(ResteasyBootstrapModule.class.getResourceAsStream("build-info.properties"));
    bind(BuildInfo.class).toInstance(buildInfo);
    bind(ServiceMetadata.class).toInstance(ServiceMetadata.create(buildInfo));
  }

  @Provides
  SwaggerConfig provideSwaggerConfig(ServiceMetadata serviceMetadata) {
    SwaggerConfig config = new SwaggerConfig();
    config.setApiVersion(serviceMetadata.getBuildInfo().getVersion());
    config.setSwaggerVersion(SwaggerSpec.version());
    return config;
  }
}
