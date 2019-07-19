package com.cerner.beadledom.integration.service;

import com.cerner.beadledom.integration.service.resource.HelloWorldResourceImpl;
import com.cerner.beadledom.integration.api.HelloWorldResource;
import com.google.inject.PrivateModule;

public class BeadledomIntegrationModule extends PrivateModule {

  protected void configure() {
    bind(HelloWorldResource.class).to(HelloWorldResourceImpl.class);

    expose(HelloWorldResource.class);
  }
}
