package com.cerner.beadledom.json.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "Standard error details")
@io.swagger.annotations.ApiModel(value = "Standard error details")
@JsonDeserialize(builder = ErrorDetail.Builder.class)
public abstract class ErrorDetail {

  @Nullable
  @JsonProperty("domain")
  @ApiModelProperty(value = "A subsystem or context where an error occurred")
  @io.swagger.annotations.ApiModelProperty(value = "A subsystem or context where an error occurred")
  public abstract String domain();

  @Nullable
  @JsonProperty("reason")
  @ApiModelProperty(value = "short name or key for an error")
  @io.swagger.annotations.ApiModelProperty(value = "short name or key for an error")
  public abstract String reason();

  @Nullable
  @JsonProperty("message")
  @ApiModelProperty(value = "Human readable description of an error")
  @io.swagger.annotations.ApiModelProperty(value = "Human readable description of an error")
  public abstract String message();

  @Nullable
  @JsonProperty("locationType")
  @ApiModelProperty(value = "Location or type of the value that caused an error")
  @io.swagger.annotations.ApiModelProperty(
      value = "Location or type of the value that caused an error")
  public abstract String locationType();

  @Nullable
  @JsonProperty("location")
  @ApiModelProperty(value = "Name of the value that caused an error")
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
