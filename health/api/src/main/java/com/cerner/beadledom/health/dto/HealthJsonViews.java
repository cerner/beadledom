package com.cerner.beadledom.health.dto;

/**
 * Jackson JSON views for health resources.
 */
public final class HealthJsonViews {
  private HealthJsonViews() {
  }

  public static class Availability {
  }

  public static class Primary extends Availability {
  }

  public static class Diagnostic extends Primary {
  }

  public static class Dependency {
  }
}
