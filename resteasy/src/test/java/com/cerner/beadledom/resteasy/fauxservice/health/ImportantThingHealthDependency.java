package com.cerner.beadledom.resteasy.fauxservice.health;

import com.cerner.beadledom.health.HealthDependency;
import com.cerner.beadledom.health.HealthStatus;

import java.util.Optional;

public class ImportantThingHealthDependency extends HealthDependency {
  public static boolean healthy = true;
  public static RuntimeException throwException = null;

  @Override
  public HealthStatus checkAvailability() {
    if (throwException != null) {
      throw throwException;
    }

    if (healthy) {
      return HealthStatus.create(200, "Fortunately, I'm fine.");
    } else {
      return HealthStatus.create(500, "Uh-oh...");
    }
  }

  @Override
  public Optional<String> getBasicAvailabilityUrl() {
    return Optional.of("http://important.url");
  }

  @Override
  public String getName() {
    return "important-thing";
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable("important-thing");
  }
}
