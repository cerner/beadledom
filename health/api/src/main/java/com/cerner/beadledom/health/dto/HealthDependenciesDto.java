package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 * Represents the list of dependencies of a service.
 *
 * <p>This can be used to construct health responses dependencies list.
 */
@AutoValue
@JsonDeserialize(builder = HealthDependenciesDto.Builder.class)
@ApiModel(description = "Indicates the list of dependencies of this service.")
@io.swagger.annotations.ApiModel(
    description = "Indicates the list of dependencies of this service.")
public abstract class HealthDependenciesDto {
  /**
   * Creates a new builder for {@code HealthDto}.
   */
  public static Builder builder() {
    return new AutoValue_HealthDependenciesDto.Builder();
  }

  @ApiModelProperty(
      "the results of any dependency health checks invoked as part of this health check")
  @io.swagger.annotations.ApiModelProperty(
      "the results of any dependency health checks invoked as part of this health check")
  @JsonProperty("dependencies")
  @JsonView(HealthJsonViews.Primary.class)
  public abstract List<HealthDependencyDto> getDependencies();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static HealthDependenciesDto.Builder create() {
      return HealthDependenciesDto.builder();
    }

    @JsonProperty("dependencies")
    public abstract Builder setDependencies(List<HealthDependencyDto> dependencies);

    public abstract HealthDependenciesDto build();
  }
}
