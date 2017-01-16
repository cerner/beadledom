package com.cerner.beadledom.health;

import com.google.auto.value.AutoValue;
import java.util.Optional;

/**
 * Represents the result of checking a service dependency's health.
 */
@AutoValue
public abstract class HealthStatus {
  /**
   * Create an instance with the given message and status code.
   */
  public static HealthStatus create(int status, String message) {
    return create(status, message, null);
  }

  /**
   * Create an instance with the given message, status code, and exception.
   */
  public static HealthStatus create(int status, String message, Throwable exception) {
    return new AutoValue_HealthStatus(message, status, Optional.ofNullable(exception));
  }

  /**
   * Returns a description of the status of the dependency (eg. `"example-service is available"`)
   */
  public abstract String getMessage();

  /**
   * Returns the HTTP status code returned by the dependency, as seen from the location of the
   * health check resource (eg. `200`).
   *
   * <p>For dependencies without an HTTP health check, this should simulate what such a health check
   * would return - e.g. 200 for healthy, 503 for unavailable.
   */
  public abstract int getStatus();

  /**
   * Returns the health check exception, or {@link Optional#empty()} if no exception occurred.
   */
  public abstract Optional<Throwable> getException();
}
