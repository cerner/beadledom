package com.cerner.beadledom.health.dto;

import static com.cerner.beadledom.health.dto.HealthJsonViews.Availability;
import static com.cerner.beadledom.health.dto.HealthJsonViews.Diagnostic;
import static com.cerner.beadledom.health.dto.HealthJsonViews.Primary;
import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.metadata.ServiceMetadata;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Optional;

/**
 * Represents the health of a service.
 *
 * <p>This can be used to construct health responses for the Basic Availability Check,
 * Primary Health Check, and Diagnostic Health Check.
 */
@ApiModel(description = "Indicates the health of this service.")
@AutoValue
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
    return new AutoValue_HealthDto.Builder(healthDto);
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
        .setScmRevision(serviceMetadata.getBuildInfo().getScmRevision())
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
  @JsonProperty("message")
  @JsonView(Availability.class)
  public abstract Optional<String> getMessage();

  /**
   * Returns the status code representing the health of the service.
   *
   * <p>This is not included in the json model since it is used as the HTTP response code.
   */
  @JsonIgnore
  public abstract Integer getStatus();

  @ApiModelProperty(
      "the results of any dependency health checks invoked as part of this health check")
  @JsonProperty("dependencies")
  @JsonView({Primary.class, Diagnostic.class, HealthJsonViews.Dependency.class})
  public abstract Optional<List<HealthDependencyDto>> getDependencies();

  @ApiModelProperty("Build and version information of the service serving the health check response")
  @JsonProperty("build")
  @JsonView(Diagnostic.class)
  public abstract Optional<BuildDto> getBuild();


  @ApiModelProperty("Runtime and environment information of the server")
  @JsonProperty("server")
  @JsonView(Diagnostic.class)
  public abstract Optional<ServerDto> getServer();

  @AutoValue.Builder
  public abstract static class Builder {

    abstract Builder setMessage(Optional<String> message);

    public Builder setMessage(String message) {
      return setMessage(Optional.ofNullable(message));
    }

    public abstract Builder setStatus(Integer status);

    abstract Builder setDependencies(Optional<List<HealthDependencyDto>> dependencies);

    public Builder setDependencies(List<HealthDependencyDto> dependencies) {
      return setDependencies(Optional.ofNullable(dependencies));
    }

    abstract Builder setbuild(Optional<BuildDto> build);

    public Builder setbuild(BuildDto build) {
      return setbuild(Optional.ofNullable(build));
    }

    abstract Builder setServer(Optional<ServerDto> server);

    public Builder setServer(ServerDto server) {
      return setServer(Optional.ofNullable(server));
    }

    public abstract HealthDto build();
  }
}
