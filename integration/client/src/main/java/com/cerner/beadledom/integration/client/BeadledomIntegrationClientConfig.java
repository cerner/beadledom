package com.cerner.beadledom.integration.client;

/**
 * Client Configuration for the example service.
 *
 * @author Nick Behrens
 */
public class BeadledomIntegrationClientConfig {
  private final String baseUrl;

  public BeadledomIntegrationClientConfig(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
