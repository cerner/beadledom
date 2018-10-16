package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 *  Contains hyperlinks to other resources related to a dependency.
 *
 *  <p>This is used to construct a health check response.
 *
 *  @since 1.4
 */
@AutoValue
@JsonDeserialize(builder = LinksDto.Builder.class)
@ApiModel(description = "Contains hyperlinks to other resources related to a dependency")
@io.swagger.annotations.ApiModel(
    description = "Contains hyperlinks to other resources related to a dependency")
public abstract class LinksDto {
  /**
   * Creates a new builder for {@code LinksDto}.
   */
  public static Builder builder() {
    return new AutoValue_LinksDto.Builder();
  }

  @ApiModelProperty("The health endpoint within a service that can be queried for the health of "
      + "this dependency")
  @io.swagger.annotations.ApiModelProperty(
      "The health endpoint within a service that can be queried for the health of this dependency")
  @JsonProperty("self")
  public abstract String getSelf();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static LinksDto.Builder create() {
      return LinksDto.builder();
    }

    public abstract Builder setSelf(String self);

    public abstract LinksDto build();
  }
}
