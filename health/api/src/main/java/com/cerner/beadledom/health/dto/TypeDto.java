package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Optional;

/**
 *  Indicates the type of dependency, which may have additional properties of a type.
 *
 *  <p>This can be used to construct a health check response.
 *
 *  @since 1.4
 */
@ApiModel(description = "Indicates the type of dependency, which may have additional properties")
@AutoValue
public abstract class TypeDto {
  /**
   * Creates a new builder for {@code TypeDto}.
   */
  public static Builder builder() {
    return new AutoValue_TypeDto.Builder()
        .setHttpService(Optional.empty());
  }

  @ApiModelProperty("Properties of the HTTP service dependency")
  @JsonProperty("httpService")
  public abstract Optional<HttpServiceDto> getHttpService();

  @AutoValue.Builder
  public abstract static class Builder {
    abstract Builder setHttpService(Optional<HttpServiceDto> httpService);

    public Builder setHttpService(HttpServiceDto httpService) {
      return setHttpService(Optional.ofNullable(httpService));
    }

    public abstract TypeDto build();
  }
}
