package com.cerner.beadledom.integration.client;

public class BeadledomIntegrationClientConfig {
  private final String baseUrl;

  // If OAuth is being used this is a natural place to encapsulate the values
  public BeadledomIntegrationClientConfig(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
