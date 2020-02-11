package com.cerner.beadledom.health.internal.presenter;

import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.health.dto.HealthDependenciesDto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wraps a HealthDependenciesDto to add methods needed by the HTML views.
 *
 * @author John Leacox
 */
public class HealthDependenciesPresenter {
  private final HealthDependenciesDto dto;

  /**
   * Creates a new instance of {@code HealthPresenter}.
   */
  public HealthDependenciesPresenter(HealthDependenciesDto dto) {
    this.dto = checkNotNull(dto, "dto: null");
  }

  /**
   * Returns the list of dependencies, wrapped with HealthDependencyPresenter.
   */
  public List<HealthDependencyPresenter> getDependencies() {
    return dto.getDependencies().stream()
        .map(HealthDependencyPresenter::new).collect(Collectors.toList());
  }
}
