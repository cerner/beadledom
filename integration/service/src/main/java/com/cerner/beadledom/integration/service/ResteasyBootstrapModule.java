package com.cerner.beadledom.integration.service;

import com.cerner.beadledom.health.HealthModule;
import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.cerner.beadledom.pagination.OffsetPaginationModule;
import com.cerner.beadledom.pagination.models.OffsetPaginationConfiguration;
import com.cerner.beadledom.resteasy.ResteasyModule;
import com.cerner.beadledom.swagger2.Swagger2Module;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.OptionalBinder;
import io.swagger.models.Info;
import javax.inject.Singleton;

/**
 * A Guice Module that installs:
 * <ul>
 *   <li>{@link ResteasyModule}</li>
 *   <li>{@link Swagger2Module}</li>
 *   <li>{@link HealthModule}</li>
 *   <li>{@link OffsetPaginationModule}</li>
 * </ul>
 *
 * @author Nick Behrens
 */
public class ResteasyBootstrapModule extends AbstractModule {

  protected void configure() {
    install(new ResteasyModule());
    install(new Swagger2Module());
    install(new HealthModule());

    BuildInfo buildInfo = BuildInfo.load(ResteasyBootstrapModule.class
        .getResourceAsStream("build-info.properties"));
    bind(BuildInfo.class).toInstance(buildInfo);
    bind(ServiceMetadata.class).toInstance(ServiceMetadata.create(buildInfo));

    OffsetPaginationConfiguration paginationConfig = OffsetPaginationConfiguration.builder()
        .setDefaultLimit(10)
        .setMaxLimit(10)
        .build();
    OptionalBinder
        .newOptionalBinder(
            binder(),
            OffsetPaginationConfiguration.class)
        .setBinding().toInstance(paginationConfig);

    install(new OffsetPaginationModule());
  }

  @Provides
  @Singleton
  Info provideSwagger2Info(ServiceMetadata serviceMetadata) {
    return new Info()
        .title("Beadledom Integration Service")
        .version(serviceMetadata.getBuildInfo().getVersion());
  }
}
