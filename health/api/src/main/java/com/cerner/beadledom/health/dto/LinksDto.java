package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *  Contains hyperlinks to other resources related to a dependency.
 *
 *  <p>This is used to construct a health check response.
 *
 *  @since 1.4
 */
@ApiModel(description = "Contains hyperlinks to other resources related to a dependency")
@Schema(description = "Contains hyperlinks to other resources related to a dependency")
@AutoValue
public abstract class LinksDto {
  /**
   * Creates a new builder for {@code LinksDto}.
   */
  public static Builder builder() {
    return new AutoValue_LinksDto.Builder();
  }

  @ApiModelProperty("The health endpoint within a service that can be queried for the health of "
      + "this dependency")
  @Schema(
      description = "The health endpoint within a service that can be queried for the health of "
          + "this dependency")
  @JsonProperty("self")
  public abstract String getSelf();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setSelf(String self);

    public abstract LinksDto build();
  }
}
