package com.cerner.beadledom.health;

import java.util.Optional;

/**
 * Represents a dependency to be checked during health checks.
 *
 * <p>HealthDependency instances <em>must</em> be thread-safe.
 */
public abstract class HealthDependency {

  private Boolean primary = false;

  /**
   * Invokes the dependency's basic availability health check (or closest equivalent).
   *
   * <p>Implementations may throw RuntimeExceptions; the health checker will handle them
   * gracefully and assume the dependency is unhealthy.
   *
   * @return the status of the dependency
   */
  public abstract HealthStatus checkAvailability();

  /**
   * Return the URL to the basic availability health check (or closest equivalent) of the service,
   * or {@link Optional#empty()} if there is none.
   *
   * @deprecated As of 2.1.1, no longer used in the display (html and json) of health endpoints.
   */
  @Deprecated
  public Optional<String> getBasicAvailabilityUrl() {
    return Optional.empty();
  }

  /**
   * Returns a Boolean to indicate if this check is a primary check for this service.
   */
  public Boolean getPrimary() {
    return primary;
  }

  /**
   * Returns a short name for the dependency, which will be used to identify it in URLs.
   *
   * <p>Examples: "external-service", "hbase". This name must be unique amongst all the
   * dependencies.
   */
  public abstract String getName();

  /**
   * Returns the description for the dependency or {@link Optional#empty()} if there is none.
   */
  public Optional<String> getDescription() {
    return Optional.empty();
  }
}
