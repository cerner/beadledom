package com.cerner.beadledom.resteasy.fauxservice;

import com.cerner.beadledom.avro.AvroJacksonGuiceModule;
import com.cerner.beadledom.avro.AvroSwaggerGuiceModule;
import com.cerner.beadledom.health.HealthDependency;
import com.cerner.beadledom.health.HealthModule;
import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.cerner.beadledom.resteasy.ResteasyModule;
import com.cerner.beadledom.resteasy.fauxservice.dao.HelloDao;
import com.cerner.beadledom.resteasy.fauxservice.health.ImportantHealthDependency2;
import com.cerner.beadledom.resteasy.fauxservice.health.ImportantThingHealthDependency;
import com.cerner.beadledom.resteasy.fauxservice.resource.HelloResource;
import com.cerner.beadledom.swagger.SwaggerModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.core.SwaggerSpec;
import java.time.Instant;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

public class FauxModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new ResteasyModule());
    install(new SwaggerModule());
    install(new AvroJacksonGuiceModule());
    install(new AvroSwaggerGuiceModule());
    install(new HealthModule());

    bind(HelloDao.class).in(Singleton.class);
    bind(HelloResource.class);

    BuildInfo buildInfo = BuildInfo.load(getClass().getResourceAsStream("build-info.properties"));
    bind(BuildInfo.class).toInstance(buildInfo);
    bind(ServiceMetadata.class).toInstance(
        ServiceMetadata.builder()
            .setBuildInfo(buildInfo)
            .setHostName("bogusbox")
            .setStartupTime(Instant.parse("2001-01-01T01:01:01Z"))
            .build()); // In a real service, use ServiceMetadata.create instead of ServiceMetadata.builder

    Multibinder<HealthDependency> healthDependencyBinder =
        Multibinder.newSetBinder(binder(), HealthDependency.class);
    healthDependencyBinder.addBinding().to(ImportantThingHealthDependency.class);

    bind(FauxLifecycleHook.class).asEagerSingleton();
  }

  @ProvidesIntoSet
  HealthDependency getDependency2() {
    return new ImportantHealthDependency2();
  }

  @Provides
  SwaggerConfig provideSwaggerConfig(ServiceMetadata serviceMetadata) {
    SwaggerConfig config = new SwaggerConfig();
    config.setApiVersion(serviceMetadata.getBuildInfo().getVersion());
    config.setSwaggerVersion(SwaggerSpec.version());
    return config;
  }

  private static class FauxLifecycleHook {
    @PostConstruct
    public void postConstruct() {
      System.out.println("**************FauxLifecycleHook postConstruct");
    }

    @PreDestroy
    public void preDestroy() {
      System.out.println("**************FauxLifecycleHook preDestroy");
    }
  }
}
