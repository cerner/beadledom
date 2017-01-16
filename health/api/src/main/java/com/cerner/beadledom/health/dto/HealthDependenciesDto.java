package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 * Represents the list of dependencies of a service.
 *
 * <p>This can be used to construct health responses dependencies list.
 */
@ApiModel(description = "Indicates the list of dependencies of this service.")
@AutoValue
public abstract class HealthDependenciesDto {
  /**
   * Creates a new builder for {@code HealthDto}.
   */
  public static Builder builder() {
    return new AutoValue_HealthDependenciesDto.Builder();
  }

  @ApiModelProperty(
      "the results of any dependency health checks invoked as part of this health check")
  @JsonProperty("dependencies")
  @JsonView(HealthJsonViews.Primary.class)
  public abstract List<HealthDependencyDto> getDependencies();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setDependencies(List<HealthDependencyDto> dependencies);

    public abstract HealthDependenciesDto build();
  }
}
