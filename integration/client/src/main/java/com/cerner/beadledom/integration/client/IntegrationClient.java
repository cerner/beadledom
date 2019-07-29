package com.cerner.beadledom.integration.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomWebTarget;
import com.cerner.beadledom.integration.api.HelloWorldResource;

/**
 * A client class that provides access to the {@link HelloWorldResource} instead of exposing the
 * jax-rs resource directly.
 *
 * @author Nick Behrens
 */
public class IntegrationClient {
  private final HelloWorldResource helloWorldResource;

  IntegrationClient(BeadledomClient client, IntegrationClientConfig config) {
    BeadledomWebTarget target = client.target(config.getBaseUrl());

    helloWorldResource = target.proxy(HelloWorldResource.class);
  }

  public HelloWorldResource helloWorldResource() {
    return helloWorldResource;
  }
}
