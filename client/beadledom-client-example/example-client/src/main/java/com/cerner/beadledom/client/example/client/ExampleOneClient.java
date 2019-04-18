package com.cerner.beadledom.client.example.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomWebTarget;

/**
 * A single client class that provides easy access to all of the resource interfaces that make up
 * the client. The main reason for providing a wrapper client class like this vs injecting the
 * resource interfaces directly, is that the injected interfaces may get picked up by
 * jax-rs/Beadledom if being used within another service.
 *
 * @author John Leacox
 */
public class ExampleOneClient {
  private final ResourceOne resourceOne;

  ExampleOneClient(BeadledomClient client, ExampleClientConfig config) {
    BeadledomWebTarget target = client.target(config.uri());

    resourceOne = target.proxy(ResourceOne.class);
  }

  public ResourceOne resourceOne() {
    return resourceOne;
  }
}
