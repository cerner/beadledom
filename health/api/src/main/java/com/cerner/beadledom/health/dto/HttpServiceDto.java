package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Optional;

/**
 *  Represents the http service dependency. This can be used to construct a health check response.
 *
 *  @since 1.4
 */
@ApiModel(description = "Properties of the HTTP service dependency")
@AutoValue
public abstract class HttpServiceDto {
  /**
   * Creates a new builder for {@code HttpServiceDto}.
   */
  public static HttpServiceDto.Builder builder() {
    return new AutoValue_HttpServiceDto.Builder();
  }

  @ApiModelProperty("Availability check URL of the remote dependency")
  @JsonProperty("url")
  public abstract String getUrl();

  @ApiModelProperty("HTTP status code returned by the service")
  @JsonProperty("status")
  public abstract Optional<Integer> getStatus();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setUrl(String url);

    abstract Builder setStatus(Optional<Integer> status);

    public Builder setStatus(Integer status) {
      return setStatus(Optional.ofNullable(status));
    }

    public abstract HttpServiceDto build();
  }
}
