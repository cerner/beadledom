package com.cerner.beadledom.health.internal.presenter;

import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.health.dto.HealthDependencyDto;
import com.cerner.beadledom.health.dto.HealthDto;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;

/**
 * Wraps a HealthDto to add methods needed by the HTML views.
 */
public class HealthPresenter {
  private final HealthDto dto;

  /**
   * Creates a new instance of {@code HealthPresenter}.
   */
  public HealthPresenter(HealthDto dto) {
    this.dto = checkNotNull(dto, "dto: null");
  }

  /**
   * Returns the list of dependencies, wrapped with HealthDependencyPresenter.
   */
  public List<HealthDependencyPresenter> getDependencies() {
    List<HealthDependencyPresenter> dependencies = Lists.newArrayList();
    if (dto.getDependencies().isPresent()) {
      for (HealthDependencyDto dependencyDto : dto.getDependencies().get()) {
        dependencies.add(new HealthDependencyPresenter(dependencyDto));
      }
    }
    return dependencies;
  }

  /**
   * Returns the BuildInfo from the DTO.
   */
  public BuildPresenter getBuildInfo() {
    return new BuildPresenter(dto.getBuild().get());
  }

  /**
   * Returns the ServerInfo from the DTO.
   */
  public ServerPresenter getServerInfo() {
    return new ServerPresenter(dto.getServer().get());
  }

  /**
   * Returns the message from the DTO.
   */
  public Optional<String> getMessage() {
    return dto.getMessage();
  }

  /**
   * Returns the CSS class indicating whether the service is healthy ("healthy") or not
   * ("unhealthy").
   */
  public String getStatusClass() {
    if (dto.getStatus() >= 200 && dto.getStatus() <= 299) {
      return "healthy";
    }
    return "unhealthy";
  }
}
