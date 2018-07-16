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
 *  Indicates the type of dependency, which may have additional properties of a type.
 *
 *  <p>This can be used to construct a health check response.
 *
 *  @since 1.4
 */
@AutoValue
@JsonDeserialize(builder = TypeDto.Builder.class)
@ApiModel(description = "Indicates the type of dependency, which may have additional properties")
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
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static TypeDto.Builder create() {
      return TypeDto.builder();
    }

    abstract Builder setHttpService(Optional<HttpServiceDto> httpService);

    @JsonProperty("httpService")
    public Builder setHttpService(HttpServiceDto httpService) {
      return setHttpService(Optional.ofNullable(httpService));
    }

    public abstract TypeDto build();
  }
}
