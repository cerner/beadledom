package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.Optional;

/**
 * Represents health of a service dependency.
 */
@ApiModel(
    description = "Provides information about a dependency of this service, such as an external "
        + "service that it relies on.")
@AutoValue
public abstract class HealthDependencyDto {

  /**
   * Creates a new builder for {@code HealthDependencyDto}.
   */
  public static Builder builder() {
    return new AutoValue_HealthDependencyDto.Builder()
        .setName(Optional.empty())
        .setPrimary(false)
        .setType(Optional.empty())
        .setHealthy(false)
        .setLinks(Optional.empty())
        .setMessage(Optional.empty());
  }

  /**
   * Creates a new builder for {@code HealthDependencyDto} with values copied from the existing
   * {@code HealthDependencyDto}.
   */
  public static Builder builder(HealthDependencyDto healthDependencyDto) {
    return healthDependencyDto.toBuilder();
  }

  @ApiModelProperty("The id of the dependency")
  @JsonProperty("id")
  public abstract String getId();

  @ApiModelProperty("Indicates the health of the dependency")
  @JsonProperty("healthy")
  @JsonView({HealthJsonViews.Primary.class, HealthJsonViews.Diagnostic.class})
  public abstract boolean isHealthy();

  @ApiModelProperty("Indicates if this is a primary dependency of the service")
  @JsonProperty("primary")
  @JsonView(HealthJsonViews.Diagnostic.class)
  public abstract boolean isPrimary();

  @ApiModelProperty("Contains hyperlinks to other resources for health check")
  @JsonProperty("links")
  public abstract Optional<LinksDto> getLinks();

  @ApiModelProperty("The display name of the dependency")
  @JsonProperty("name")
  public abstract Optional<String> getName();

  @ApiModelProperty("Indicates the type of dependency, which may have additional properties of that"
      + " type")
  @JsonProperty("type")
  public abstract Optional<TypeDto> getType();

  @ApiModelProperty("A human readable explanation of the health check status")
  @JsonProperty("message")
  public abstract Optional<String> getMessage();

  /**
   * Returns a builder with same property values as this; allowing modification of some values.
   */
  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setId(String id);

    public abstract Builder setHealthy(boolean health);

    abstract Builder setLinks(Optional<LinksDto> links);

    public Builder setLinks(LinksDto links) {
      return setLinks(Optional.ofNullable(links));
    }

    abstract Builder setName(Optional<String> name);

    public Builder setName(String name) {
      return setName(Optional.ofNullable(name));
    }

    public abstract Builder setPrimary(boolean primary);

    abstract Builder setType(Optional<TypeDto> type);

    public Builder setType(TypeDto type) {
      return setType(Optional.ofNullable(type));
    }

    abstract Builder setMessage(Optional<String> message);

    public Builder setMessage(String message) {
      return setMessage(Optional.ofNullable(message));
    }

    public abstract HealthDependencyDto build();
  }
}
