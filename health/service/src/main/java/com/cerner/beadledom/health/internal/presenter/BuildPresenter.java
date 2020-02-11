package com.cerner.beadledom.health.internal.presenter;

import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.health.dto.BuildDto;
import java.util.Optional;

/**
 * Wraps a BuildDto to add methods needed by the HTML views.
 *
 * @author Nimesh Subramanian
 * @since 1.4
 */
public class BuildPresenter {

  private final BuildDto buildInfo;

  /**
   * Creates an instance of {@code BuildPresenter}.
   */
  public BuildPresenter(BuildDto dto) {
    this.buildInfo = checkNotNull(dto, "dto: null");
  }

  /**
   * Returns the artifact name from the DTO.
   */
  public Optional<String> getArtifactName() {
    return buildInfo.getArtifactName();
  }

  /**
   * Returns the version from the DTO.
   */
  public Optional<String> getVersion() {
    return buildInfo.getVersion();
  }

  /**
   * Returns the build date time from the DTO.
   */
  public String getBuildDateTime() {
    return buildInfo.getBuildDateTime().map(buildDateTime -> buildDateTime).orElse("Not Available");
  }
}
