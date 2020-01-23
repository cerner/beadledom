package com.cerner.beadledom.json.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

/**
 * Standard error details body for RESTful APIs.
 *
 * @author Brian van de Boogaard
 * @since 2.6
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "domain",
    "reason",
    "message",
    "locationType",
    "location"})
@AutoValue
@io.swagger.annotations.ApiModel(value = "Standard error details")
@JsonDeserialize(builder = ErrorDetail.Builder.class)
public abstract class ErrorDetail {

  @Nullable
  @JsonProperty("domain")
  @io.swagger.annotations.ApiModelProperty(value = "A subsystem or context where an error occurred")
  public abstract String domain();

  @Nullable
  @JsonProperty("reason")
  @io.swagger.annotations.ApiModelProperty(value = "short name or key for an error")
  public abstract String reason();

  @Nullable
  @JsonProperty("message")
  @io.swagger.annotations.ApiModelProperty(value = "Human readable description of an error")
  public abstract String message();

  @Nullable
  @JsonProperty("locationType")
  @io.swagger.annotations.ApiModelProperty(
      value = "Location or type of the value that caused an error")
  public abstract String locationType();

  @Nullable
  @JsonProperty("location")
  @io.swagger.annotations.ApiModelProperty(value = "Name of the value that caused an error")
  public abstract String location();

  public static Builder builder() {
    return new AutoValue_ErrorDetail.Builder();
  }

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder {

    @JsonCreator
    private static Builder create() {
      return ErrorDetail.builder();
    }

    @JsonProperty("domain")
    public abstract Builder domain(String domain);

    @JsonProperty("reason")
    public abstract Builder reason(String reason);

    @JsonProperty("message")
    public abstract Builder message(String message);

    @JsonProperty("locationType")
    public abstract Builder locationType(String locationType);

    @JsonProperty("location")
    public abstract Builder location(String location);

    public abstract ErrorDetail build();
  }
}
