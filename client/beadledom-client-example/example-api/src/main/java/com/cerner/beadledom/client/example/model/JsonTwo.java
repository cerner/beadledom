package com.cerner.beadledom.client.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * JsonTwo example model.
 *
 * @author John Leacox
 */
@AutoValue
public abstract class JsonTwo {

  /**
   * Creates a new instance of JsonOne.
   */
  @JsonCreator
  public static JsonTwo create(
      @JsonProperty("two") String two,
      @JsonProperty("hello") String hello) {
    return new AutoValue_JsonTwo(two, hello);
  }

  @JsonProperty("two")
  public abstract String getTwo();

  @JsonProperty("hello")
  public abstract String getHello();
}
