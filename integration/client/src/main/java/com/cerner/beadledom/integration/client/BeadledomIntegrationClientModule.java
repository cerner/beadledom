package com.cerner.beadledom.integration.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientConfiguration;
import com.cerner.beadledom.client.BeadledomClientModule;
import com.cerner.beadledom.client.BeadledomWebTarget;
import com.cerner.beadledom.client.jackson.ObjectMapperClientFeatureModule;
import com.cerner.beadledom.integration.api.HelloWorldResource;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import javax.inject.Singleton;

public class BeadledomIntegrationClientModule extends PrivateModule {

  @Override
  public void configure() {
    requireBinding(BeadledomIntegrationClientConfig.class);

    install(BeadledomClientModule.with(BeadledomIntegrationClientFeature.class));
    install(ObjectMapperClientFeatureModule.with(BeadledomIntegrationClientFeature.class));

    expose(HelloWorldResource.class);
  }

  @Provides
  @Singleton
  @BeadledomIntegrationClientFeature
  public BeadledomClientConfiguration provideClientConfiguration() {
    return BeadledomClientConfiguration.builder().build();
  }

  @Provides
  @Singleton
  public HelloWorldResource provideHelloWorldResource(BeadledomWebTarget target) {
    return target.proxy(HelloWorldResource.class);
  }

  @Provides
  @Singleton
  public BeadledomWebTarget provideBeadledomWebTarget(
      @BeadledomIntegrationClientFeature BeadledomClient client, BeadledomIntegrationClientConfig config) {
    return client.target(config.getBaseUrl());
  }
}
