package com.cerner.beadledom.health;

import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import java.time.Instant;
import java.util.Properties;
import javax.ws.rs.core.UriInfo;

/**
 * Test Module for Resources.
 */
public class TestModule1 extends AbstractModule {
  private UriInfo uri;

  @Override
  public void configure() {
    install(new HealthModule());
  }

  @ProvidesIntoSet
  HealthDependency provideMyHealthDependency1() {
    return new HealthDependency1();
  }

  @ProvidesIntoSet
  HealthDependency provideMyHealthDependency2() {
    return new HealthDependency2();
  }

  @Provides
  ServiceMetadata provideServiceMetadata() {
    return ServiceMetadata.builder()
                .setBuildInfo(BuildInfo.builder()
                    .setArtifactId("Lombre")
                    .setGroupId("Pyroar")
                    .setScmRevision("Aegislash")
                    .setBuildDateTime("Lugia")
                    .setRawProperties(new Properties())
                    .setVersion("Qwilfish")
                    .build())
                .setStartupTime(Instant.now())
                .build();
  }
}
