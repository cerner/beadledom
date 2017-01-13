package com.cerner.beadledom.health.internal.presenter;

import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.health.dto.HealthDependencyDto;
import java.util.Optional;

/**
 * Wraps an HealthDependencyDto to add methods needed by the HTML views.
 */
public class HealthDependencyPresenter {
  private final HealthDependencyDto dto;

  /**
   * Creates an instance of {@code HealthDependencyPresenter}.
   */
  public HealthDependencyPresenter(HealthDependencyDto dto) {
    this.dto = checkNotNull(dto, "dto: null");
  }

  /**
   * Returns the name from the DTO.
   */
  public String getName() {
    return dto.getId();
  }

  /**
   * Returns the meta/health endpoint for the service from the DTO.
   */
  public Optional<String> getLink() {
    return dto.getLinks().map(links -> links.getSelf());
  }

  /**
   * Returns the message from the DTO.
   */
  public Optional<String> getMessage() {
    return dto.getMessage();
  }

  /**
   * Returns a CSS class indicating whether the dependency is healthy ("healthy") or not
   * ("unhealthy").
   */
  public String getStatusClass() {
    return dto.isHealthy() ? "healthy" : "unhealthy";
  }

  /**
   * Returns display text indicating whether the dependency is "Available" or "Unavailable".
   */
  public String getStatusText() {
    return dto.isHealthy() ? "Available" : "Unavailable";
  }
}
