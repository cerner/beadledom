package com.cerner.beadledom.client.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

/**
 * JsonOne example model.
 *
 * @author John Leacox
 */
@AutoValue
@JsonDeserialize(builder = JsonOne.Builder.class)
public abstract class JsonOne {

  public static Builder builder() {
    return new AutoValue_JsonOne.Builder();
  }

  /**
   * Creates a new instance of JsonOne.
   */
  @JsonCreator
  public static JsonOne create(
      @JsonProperty("one") String one,
      @JsonProperty("hello") String hello) {
    return builder()
        .setOne(one)
        .setHello(hello)
        .build();
  }

  @JsonProperty("one")
  public abstract String getOne();

  @JsonProperty("hello")
  public abstract String getHello();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "set")
  public abstract static class Builder {

    @JsonCreator
    private static Builder create() {
      return JsonOne.builder();
    }

    public abstract Builder setOne(String one);

    public abstract Builder setHello(String hello);

    public abstract JsonOne build();
  }
}
