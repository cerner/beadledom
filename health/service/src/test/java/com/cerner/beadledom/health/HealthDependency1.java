package com.cerner.beadledom.health;

import java.util.Optional;

/**
 * Health Dependency 1.
 */
public class HealthDependency1 extends HealthDependency {
  @Override
  public HealthStatus checkAvailability() {
    return HealthStatus.create(200, "HealthDependency1 is available");
  }

  @Override
  public String getName() {
    return "HealthDependency1";
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable("HealthDependency1");
  }
}
