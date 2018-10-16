package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
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
@AutoValue
@JsonDeserialize(builder = BuildDto.Builder.class)
@ApiModel(description = "Service build and version information.")
@io.swagger.annotations.ApiModel(
    description = "Service build and version information.")
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
  @io.swagger.annotations.ApiModelProperty("The name of the artifact")
  @JsonProperty("artifactName")
  public abstract Optional<String> getArtifactName();

  @ApiModelProperty("The version of the service which served this health check response")
  @io.swagger.annotations.ApiModelProperty(
      "The version of the service which served this health check response")
  @JsonProperty("version")
  public abstract Optional<String> getVersion();

  @ApiModelProperty("The build date/time of the service or application in ISO-8601 format")
  @io.swagger.annotations.ApiModelProperty(
      "The build date/time of the service or application in ISO-8601 format")
  @JsonProperty("buildDateTime")
  public abstract Optional<String> getBuildDateTime();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static BuildDto.Builder create() {
      return BuildDto.builder();
    }

    abstract Builder setArtifactName(Optional<String> artifactName);

    @JsonProperty("artifactName")
    public Builder setArtifactName(String artifactName) {
      return setArtifactName(Optional.ofNullable(artifactName));
    }

    abstract Builder setVersion(Optional<String> version);

    @JsonProperty("version")
    public Builder setVersion(String version) {
      return setVersion(Optional.ofNullable(version));
    }

    abstract Builder setBuildDateTime(Optional<String> buildDateTime);

    @JsonProperty("buildDateTime")
    public Builder setBuildDateTime(String buildDateTime) {
      return setBuildDateTime(Optional.ofNullable(buildDateTime));
    }

    public abstract BuildDto build();
  }
}
