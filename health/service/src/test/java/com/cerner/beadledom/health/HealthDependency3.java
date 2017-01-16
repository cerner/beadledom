package com.cerner.beadledom.health;

import java.util.Optional;

/**
 * Health Dependency 3.
 */
public class HealthDependency3 extends HealthDependency {
  @Override
  public HealthStatus checkAvailability() {
    return HealthStatus.create(503, "HealthDependency3 is unavailable");
  }

  @Override
  public String getName() {
    return "HealthDependency3";
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable("HealthDependency3");
  }
}
