package com.cerner.beadledom.health;

import java.util.Optional;

/**
 * Represents a dependency to be checked during health checks.
 *
 * <p>HealthDependency instances <em>must</em> be thread-safe.
 */
public abstract class HealthDependency {

  private Boolean primary;

  /**
   * A constructor for Heath Dependencies that sets the dependency to be primary.
   */
  public HealthDependency() {
    primary = true;
  }

  /**
   * A constructor that allows the user to specify if the dependency is primary or not.
   *
   * @param primary boolean to indicate if the health dependency is primary
   */
  public HealthDependency(Boolean primary) {
    this.primary = primary;
  }

  /**
   * Invokes the dependency's basic availability health check (or closest equivalent).
   *
   * <p>Implementations may throw RuntimeExceptions; the health checker will handle them gracefully
   * and assume the dependency is unhealthy.
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
   * Returns true if this is a primary dependency; otherwise false.
   *
   * @deprecated as of 3.2, in favor of using {@link #isPrimary()} to access the stored primary
   *     value. It is also preferred to use constructors {@link #HealthDependency()} or
   *     {@link #HealthDependency(Boolean)} to set primary value rather than overriding this method.
   */
  @Deprecated
  public Boolean getPrimary() {
    return primary;
  }

  /**
   * Returns true if this is a primary dependency; otherwise false.
   */
  public final Boolean isPrimary() {
    return getPrimary();
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
