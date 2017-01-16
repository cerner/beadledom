package com.cerner.beadledom.client.example.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientModule;
import com.cerner.beadledom.client.BeadledomWebTarget;
import com.cerner.beadledom.client.example.ResourceTwo;
import com.cerner.beadledom.client.jackson.ObjectMapperClientFeatureModule;
import com.cerner.beadledom.jackson.SerializationFeatureFlag;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * Guice module for the Example client.
 *
 * <p>Normally a service would have only one module that provides all resource proxies. This example
 * only splits apart the two resources to show what two separate clients would look like and so that
 * testing can be done to make sure two clients can co-exist in a single consuming project.
 *
 * @author John Leacox
 */
public class ResourceTwoModule extends AbstractModule {
  @Override
  protected void configure() {
    // Bind the client module and object mapper module with this client's binding annotation so
    // that the client and object mapper bindings are namespaced for this ResourceTwo client only.
    install(BeadledomClientModule.with(ResourceTwoFeature.class));
    install(ObjectMapperClientFeatureModule.with(ResourceTwoFeature.class));

    // Consumers of the client should provide the configuration.
    requireBinding(ExampleClientConfig.class);
  }

  @Provides
  @Singleton
  ResourceTwo provideResourceTwo(
      @ResourceTwoFeature BeadledomClient beadledomClient, ExampleClientConfig config) {
    BeadledomWebTarget target = beadledomClient.target(config.uri());
    return target.proxy(ResourceTwo.class);
  }

  @ProvidesIntoSet
  @ResourceTwoFeature
  SerializationFeatureFlag getSerializationFeature() {
    return SerializationFeatureFlag.create(SerializationFeature.INDENT_OUTPUT, true);
  }
}
