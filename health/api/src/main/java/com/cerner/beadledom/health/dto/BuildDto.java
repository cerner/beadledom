package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Optional;

/**
 * Represents the build and version information of the service serving the health check response.
 *
 * <p>This can be used to construct health dependency response.
 *
 * @since 1.4
 */
@ApiModel(description = "Indicates the list of dependencies of this service.")
@AutoValue
public abstract class BuildDto {
  /**
   * Creates a new builder for {@code BuildDto}.
   */
  public static Builder builder() {
    return new AutoValue_BuildDto.Builder()
        .setArtifactName(Optional.empty())
        .setBuildDateTime(Optional.empty())
        .setVersion(Optional.empty());
  }

  @ApiModelProperty("The name of the artifact")
  @JsonProperty("artifactName")
  public abstract Optional<String> getArtifactName();

  @ApiModelProperty("The version of the service which served this health check response")
  @JsonProperty("version")
  public abstract Optional<String> getVersion();

  @ApiModelProperty("The build date/time of the service or application in ISO-8601 format")
  @JsonProperty("buildDateTime")
  public abstract Optional<String> getBuildDateTime();

  @AutoValue.Builder
  public abstract static class Builder {

    abstract Builder setArtifactName(Optional<String> artifactName);

    public Builder setArtifactName(String artifactName) {
      return setArtifactName(Optional.ofNullable(artifactName));
    }

    abstract Builder setVersion(Optional<String> version);

    public Builder setVersion(String version) {
      return setVersion(Optional.ofNullable(version));
    }

    abstract Builder setBuildDateTime(Optional<String> buildDateTime);

    public Builder setBuildDateTime(String buildDateTime) {
      return setBuildDateTime(Optional.ofNullable(buildDateTime));
    }

    public abstract BuildDto build();
  }
}
