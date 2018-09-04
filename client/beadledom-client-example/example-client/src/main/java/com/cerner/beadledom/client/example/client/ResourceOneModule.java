package com.cerner.beadledom.client.example.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientConfiguration;
import com.cerner.beadledom.client.BeadledomClientModule;
import com.cerner.beadledom.client.BeadledomWebTarget;
import com.cerner.beadledom.client.example.PaginatedClientResource;
import com.cerner.beadledom.client.example.ResourceOne;
import com.cerner.beadledom.client.jackson.ObjectMapperClientFeatureModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Guice module for the Example client.
 *
 * <p>Normally a service would have only one module that provides all resource proxies. This example
 * only splits apart the two resources to show what two separate clients would look like and so that
 * testing can be done to make sure two clients can co-exist in a single consuming project.
 *
 * @author John Leacox
 */
public class ResourceOneModule extends AbstractModule {
  @Override
  protected void configure() {
    // Bind the client module and object mapper module with this client's binding annotation so
    // that the client and object mapper bindings are namespaced for this ResourceOne client only.

    install(BeadledomClientModule.with(ResourceOneFeature.class));
    install(ObjectMapperClientFeatureModule.with(ResourceOneFeature.class));

    // Consumers of the client should provide the configuration.
    requireBinding(ExampleClientConfig.class);
  }

  @Provides
  @Singleton
  ResourceOne provideResourceOne(
      @ResourceOneFeature BeadledomClient client, ExampleClientConfig config) {
    BeadledomWebTarget target = client.target(config.uri());
    return target.proxy(ResourceOne.class);
  }

  @Provides
  @Singleton
  PaginatedClientResource providePaginatedResource(
      @ResourceOneFeature BeadledomClient client, ExampleClientConfig config) {
    BeadledomWebTarget target = client.target(config.uri());
    return target.proxy(PaginatedClientResource.class);
  }

  // Overriding the default BeadledomClientConfiguration
  @Provides
  @ResourceOneFeature
  BeadledomClientConfiguration provideClientConfig() {
    return BeadledomClientConfiguration.builder()
        .maxPooledPerRouteSize(60)
        .socketTimeoutMillis(60)
        .connectionTimeoutMillis(60)
        .ttlMillis(60)
        .connectionPoolSize(60).build();
  }
}
