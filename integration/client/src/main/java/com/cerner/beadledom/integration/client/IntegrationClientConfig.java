package com.cerner.beadledom.integration.client;

/**
 * Client Configuration for the example service.
 *
 * @author Nick Behrens
 */
public class IntegrationClientConfig {
  private final String baseUrl;

  public IntegrationClientConfig(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
