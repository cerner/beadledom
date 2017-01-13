package com.cerner.beadledom.health;

import java.util.Optional;

/**
 * Health Dependency 2.
 */
public class HealthDependency2 extends HealthDependency {
  @Override
  public HealthStatus checkAvailability() {
    return HealthStatus.create(200, "HealthDependency2 is available");
  }

  @Override
  public String getName() {
    return "HealthDependency2";
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable("HealthDependency2");
  }
}
