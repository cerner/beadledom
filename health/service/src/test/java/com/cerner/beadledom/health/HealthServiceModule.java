package com.cerner.beadledom.health;

import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.time.Instant;
import java.util.Properties;

/**
 * A Module that installs the health module and provides the service metadata
 *
 * @author Nick Behrens
 */
public class HealthServiceModule extends AbstractModule {
  @Override
  public void configure() {
    install(new HealthModule());
  }

  @Provides
  ServiceMetadata provideServiceMetadata() {
    return ServiceMetadata.builder()
        .setBuildInfo(
            BuildInfo.builder()
                .setArtifactId("myService")
                .setGroupId("com.cerner")
                .setScmRevision("DDEA1116D6FC2A50A3788F43B9D9A9F0754D1746")
                .setBuildDateTime("2009-06-15T13:45:30")
                .setRawProperties(new Properties())
                .setVersion("1.3")
                .build())
        .setStartupTime(Instant.now())
        .build();
  }
}
