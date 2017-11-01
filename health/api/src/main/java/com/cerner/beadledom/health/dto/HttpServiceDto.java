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
 *  Represents the http service dependency. This can be used to construct a health check response.
 *
 *  @since 1.4
 */
@AutoValue
@JsonDeserialize(builder = HttpServiceDto.Builder.class)
@ApiModel(description = "Properties of the HTTP service dependency")
@io.swagger.annotations.ApiModel(description = "Properties of the HTTP service dependency")
public abstract class HttpServiceDto {
  /**
   * Creates a new builder for {@code HttpServiceDto}.
   */
  public static HttpServiceDto.Builder builder() {
    return new AutoValue_HttpServiceDto.Builder();
  }

  @ApiModelProperty("Availability check URL of the remote dependency")
  @io.swagger.annotations.ApiModelProperty("Availability check URL of the remote dependency")
  @JsonProperty("url")
  public abstract String getUrl();

  @ApiModelProperty("HTTP status code returned by the service")
  @io.swagger.annotations.ApiModelProperty("HTTP status code returned by the service")
  @JsonProperty("status")
  public abstract Optional<Integer> getStatus();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static HttpServiceDto.Builder create() {
      return HttpServiceDto.builder();
    }

    public abstract Builder setUrl(String url);

    abstract Builder setStatus(Optional<Integer> status);

    @JsonProperty("status")
    public Builder setStatus(Integer status) {
      return setStatus(Optional.ofNullable(status));
    }

    public abstract HttpServiceDto build();
  }
}
