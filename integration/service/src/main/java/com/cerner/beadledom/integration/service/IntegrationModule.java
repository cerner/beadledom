package com.cerner.beadledom.integration.service;

import com.cerner.beadledom.integration.service.resource.HelloWorldResource;
import com.google.inject.PrivateModule;

/**
 * A Guice module that binds and exposes the {@link HelloWorldResource}.
 *
 * @author Nick Behrens
 */
public class IntegrationModule extends PrivateModule {

  protected void configure() {
    bind(HelloWorldResource.class);

    expose(HelloWorldResource.class);
  }
}
