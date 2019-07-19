package com.cerner.beadledom.integration.service;

import com.cerner.beadledom.health.HealthModule;
import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.cerner.beadledom.resteasy.ResteasyModule;
import com.cerner.beadledom.swagger2.Swagger2Module;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.swagger.models.Info;
import javax.inject.Singleton;

public class ResteasyBootstrapModule extends AbstractModule {

  protected void configure() {
    install(new ResteasyModule());
    install(new Swagger2Module());
    install(new HealthModule());

    BuildInfo buildInfo = BuildInfo.load(ResteasyBootstrapModule.class
        .getResourceAsStream("build-info.properties"));
    bind(BuildInfo.class).toInstance(buildInfo);
    bind(ServiceMetadata.class).toInstance(ServiceMetadata.create(buildInfo));
  }

  @Provides
  @Singleton
  Info provideSwagger2Info(ServiceMetadata serviceMetadata) {
    return new Info()
        .title("Beadledom Integration Service")
        .version(serviceMetadata.getBuildInfo().getVersion());
  }
}
