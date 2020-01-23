package com.cerner.beadledom.json.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Standard error body for RESTful APIs.
 *
 * @author Brian van de Boogaard
 * @since 2.6
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "errors",
    "code",
    "message"})
@AutoValue
@io.swagger.annotations.ApiModel(value = "Standard error body")
@JsonDeserialize(builder = JsonError.Builder.class)
public abstract class JsonError {

  @JsonProperty("message")
  @io.swagger.annotations.ApiModelProperty(value = "Human readable description of an error")
  public abstract String message();

  @JsonProperty("code")
  @io.swagger.annotations.ApiModelProperty(
      value = "HTTP response status code representing the error")
  public abstract int code();

  @Nullable
  @JsonProperty("errors")
  @io.swagger.annotations.ApiModelProperty(value = "List of additional error details")
  public abstract List<ErrorDetail> errors();

  public static Builder builder() {
    return new AutoValue_JsonError.Builder();
  }

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder {

    @JsonCreator
    private static Builder create() {
      return JsonError.builder();
    }

    @JsonProperty("message")
    public abstract Builder message(String value);

    @JsonProperty("code")
    public abstract Builder code(int value);

    @JsonProperty("errors")
    public abstract Builder errors(List<ErrorDetail> value);

    public abstract JsonError build();
  }
}
