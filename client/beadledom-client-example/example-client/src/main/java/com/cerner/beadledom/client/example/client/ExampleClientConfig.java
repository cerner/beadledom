package com.cerner.beadledom.client.example.client;

/**
 * Configuration for the Example client.
 *
 * @author John Leacox
 */
public class ExampleClientConfig {
  private final String uri;

  public ExampleClientConfig(String uri) {
    this.uri = uri;
  }

  public String uri() {
    return uri;
  }
}
