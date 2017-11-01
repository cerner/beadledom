package com.cerner.beadledom.health.dto;

import static com.cerner.beadledom.health.dto.HealthJsonViews.Availability;
import static com.cerner.beadledom.health.dto.HealthJsonViews.Diagnostic;
import static com.cerner.beadledom.health.dto.HealthJsonViews.Primary;
import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.metadata.ServiceMetadata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Represents the health of a service.
 *
 * <p>This can be used to construct health responses for the Basic Availability Check,
 * Primary Health Check, and Diagnostic Health Check.
 */
@AutoValue
@JsonDeserialize(builder = HealthDto.Builder.class)
@ApiModel(description = "Indicates the health of this service.")
@io.swagger.annotations.ApiModel(description = "Indicates the health of this service.")
public abstract class HealthDto {
  /**
   * Creates a new builder for {@code HealthDto}.
   */
  public static Builder builder() {
    return new AutoValue_HealthDto.Builder()
        .setDependencies(Optional.empty())
        .setMessage(Optional.empty())
        .setbuild(Optional.empty())
        .setServer(Optional.empty());
  }

  /**
   * Creates a new builder for {@code HealthDto} with values copied from the existing
   * {@code HealthDto}.
   */
  public static Builder builder(HealthDto healthDto) {
    return healthDto.toBuilder();
  }

  /**
   * Creates a builder for {@code HealthDto} initialized with the properties of the given metadata.
   */
  public static Builder builder(ServiceMetadata serviceMetadata) {
    checkNotNull(serviceMetadata, "serviceMetadata: null");
    ServerDto server = ServerDto.builder()
        .setHostName(serviceMetadata.getHostName())
        .setStartupDateTime(serviceMetadata.getStartupTime())
        .build();
    BuildDto build = BuildDto.builder()
        .setArtifactName(serviceMetadata.getBuildInfo().getArtifactId())
        .setVersion(serviceMetadata.getBuildInfo().getVersion())
        .setBuildDateTime(serviceMetadata.getBuildInfo().getBuildDateTime())
        .build();
    return new AutoValue_HealthDto.Builder()
        .setServer(server)
        .setbuild(build)
        .setMessage(Optional.empty())
        .setDependencies(Optional.empty());
  }

  /**
   * Returns a description of the status of the service (e.g. "example-service is unavailable").
   *
   * <p>This field may also include further potentially useful details, such as debugging
   * information.
   */
  @ApiModelProperty(value = "a human-readable status description", required = true)
  @io.swagger.annotations.ApiModelProperty(value = "a human-readable status description",
      required = true)
  @JsonProperty("message")
  @JsonView(Availability.class)
  public abstract Optional<String> getMessage();

  /**
   * Returns the status code representing the health of the service.
   *
   * <p>This is not included in the json model since it is used as the HTTP response code.
   */
  @JsonIgnore
  @Nullable
  public abstract Integer getStatus();

  @ApiModelProperty(
      "the results of any dependency health checks invoked as part of this health check")
  @io.swagger.annotations.ApiModelProperty(
      "the results of any dependency health checks invoked as part of this health check")
  @JsonProperty("dependencies")
  @JsonView({Primary.class, Diagnostic.class, HealthJsonViews.Dependency.class})
  public abstract Optional<List<HealthDependencyDto>> getDependencies();

  @ApiModelProperty(
      "Build and version information of the service serving the health check response")
  @io.swagger.annotations.ApiModelProperty(
      "Build and version information of the service serving the health check response")
  @JsonProperty("build")
  @JsonView(Diagnostic.class)
  public abstract Optional<BuildDto> getBuild();

  @ApiModelProperty("Runtime and environment information of the server")
  @io.swagger.annotations.ApiModelProperty("Runtime and environment information of the server")
  @JsonProperty("server")
  @JsonView(Diagnostic.class)
  public abstract Optional<ServerDto> getServer();

  /**
   * Returns a builder with same property values as this; allowing modification of some values.
   */
  public abstract Builder toBuilder();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static HealthDto.Builder create() {
      return HealthDto.builder();
    }

    abstract Builder setMessage(Optional<String> message);

    @JsonProperty("message")
    public Builder setMessage(String message) {
      return setMessage(Optional.ofNullable(message));
    }

    public abstract Builder setStatus(Integer status);

    abstract Builder setDependencies(Optional<List<HealthDependencyDto>> dependencies);

    @JsonProperty("dependencies")
    public Builder setDependencies(List<HealthDependencyDto> dependencies) {
      return setDependencies(Optional.ofNullable(dependencies));
    }

    abstract Builder setbuild(Optional<BuildDto> build);

    @JsonProperty("build")
    public Builder setbuild(BuildDto build) {
      return setbuild(Optional.ofNullable(build));
    }

    abstract Builder setServer(Optional<ServerDto> server);

    @JsonProperty("server")
    public Builder setServer(ServerDto server) {
      return setServer(Optional.ofNullable(server));
    }

    public abstract HealthDto build();
  }
}
