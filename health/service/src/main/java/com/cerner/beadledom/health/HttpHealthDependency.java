package com.cerner.beadledom.health;

import java.io.IOException;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of {@link HealthDependency} to check the health of any HTTP service.
 */
public class HttpHealthDependency extends HealthDependency {
  private final String availabilityUrl;
  private final String name;
  private final OkHttpClient client;

  /**
   * Constructs an instance of {@link HttpHealthDependency} with the given parameters.
   *
   * @param availabilityUrl String representing URL of the availability health check.
   * @param name String representing name of the dependency
   * @param isPrimary Boolean representing whether the dependency is primary or not.
   */
  public HttpHealthDependency(String availabilityUrl, String name, boolean isPrimary) {
    super(isPrimary);
    Objects.requireNonNull(availabilityUrl);
    Objects.requireNonNull(name);

    this.availabilityUrl = availabilityUrl;
    this.name = name;
    this.client = new OkHttpClient.Builder().build();
  }

  @Override
  public HealthStatus checkAvailability() {
    Request request = new Request.Builder().url(availabilityUrl).head().build();

    try (Response response = client.newCall(request).execute()) {
      if (response.isSuccessful()) {
        return HealthStatus.create(200, getName() + " is available.");
      }
      return HealthStatus.create(503, getName() + " is not available.");
    } catch (IOException e) {
      return HealthStatus.create(503, getName() + " is not available.");
    }
  }

  @Override
  public String getName() {
    return name;
  }
}
