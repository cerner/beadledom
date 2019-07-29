package com.cerner.beadledom.integration.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientConfiguration;
import com.cerner.beadledom.client.BeadledomClientModule;
import com.cerner.beadledom.client.BeadledomWebTarget;
import com.cerner.beadledom.client.jackson.ObjectMapperClientFeatureModule;
import com.cerner.beadledom.integration.api.HelloWorldResource;
import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import javax.inject.Singleton;

/**
 * An example Client Module.
 *
 * @author Nick Behrens
 */
public class BeadledomIntegrationClientModule extends AbstractModule {

  @Override
  public void configure() {
    requireBinding(BeadledomIntegrationClientConfig.class);

    install(BeadledomClientModule.with(BeadledomIntegrationClientFeature.class));
    install(ObjectMapperClientFeatureModule.with(BeadledomIntegrationClientFeature.class));
  }

  @Provides
  @Singleton
  public BeadledomIntegrationClient provideBeadledomIntegrationClient(
      @BeadledomIntegrationClientFeature BeadledomClient client, BeadledomIntegrationClientConfig config) {
    return new BeadledomIntegrationClient(client, config);
  }
}
