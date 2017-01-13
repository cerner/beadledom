package com.cerner.beadledom.client.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * JsonOne example model.
 *
 * @author John Leacox
 */
@AutoValue
public abstract class JsonOne {

  /**
   * Creates a new instance of JsonOne.
   */
  @JsonCreator
  public static JsonOne create(
      @JsonProperty("one") String one,
      @JsonProperty("hello") String hello) {
    return new AutoValue_JsonOne(one, hello);
  }

  @JsonProperty("one")
  public abstract String getOne();

  @JsonProperty("hello")
  public abstract String getHello();
}
