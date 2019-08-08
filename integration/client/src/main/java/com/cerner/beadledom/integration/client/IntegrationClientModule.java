package com.cerner.beadledom.integration.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientModule;
import com.cerner.beadledom.client.jackson.ObjectMapperClientFeatureModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;

/**
 * An example Client Module.
 *
 * @author Nick Behrens
 */
public class IntegrationClientModule extends AbstractModule {

  @Override
  public void configure() {
    requireBinding(IntegrationClientConfig.class);

    install(BeadledomClientModule.with(IntegrationClientFeature.class));
    install(ObjectMapperClientFeatureModule.with(IntegrationClientFeature.class));
  }

  @Provides
  @Singleton
  public IntegrationClient provideBeadledomIntegrationClient(
      @IntegrationClientFeature BeadledomClient client, IntegrationClientConfig config) {
    return new IntegrationClient(client, config);
  }
}
