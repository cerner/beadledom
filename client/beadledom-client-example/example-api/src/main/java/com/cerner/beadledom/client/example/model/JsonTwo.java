package com.cerner.beadledom.client.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

/**
 * JsonTwo example model.
 *
 * @author John Leacox
 */
@AutoValue
@JsonDeserialize(builder = JsonTwo.Builder.class)
public abstract class JsonTwo {

  public static Builder builder() {
    return new AutoValue_JsonTwo.Builder();
  }

  /**
   * Creates a new instance of JsonOne.
   */
  @JsonCreator
  public static JsonTwo create(
      @JsonProperty("two") String two,
      @JsonProperty("hello") String hello) {
    return builder()
        .setTwo(two)
        .setHello(hello)
        .build();
  }

  @JsonProperty("two")
  public abstract String getTwo();

  @JsonProperty("hello")
  public abstract String getHello();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static Builder create() {
      return JsonTwo.builder();
    }

    public abstract Builder setTwo(String two);

    public abstract Builder setHello(String hello);

    public abstract JsonTwo build();
  }
}
