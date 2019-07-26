package com.cerner.beadledom.integration.service;

import com.cerner.beadledom.integration.service.resource.HelloWorldResourceImpl;
import com.google.inject.PrivateModule;

public class BeadledomIntegrationModule extends PrivateModule {

  protected void configure() {
    bind(HelloWorldResourceImpl.class);

    expose(HelloWorldResourceImpl.class);
  }
}
