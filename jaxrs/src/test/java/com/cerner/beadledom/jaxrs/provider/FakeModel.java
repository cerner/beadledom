package com.cerner.beadledom.jaxrs.provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 * Basic model used for testing simple cases.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "id",
    "name",
    "times",
    "tags",
    "inner_models"
})
public class FakeModel {
  @JsonProperty("id")
  public String id;
  @JsonProperty("name")
  public String name;
  @JsonProperty("times")
  public int times;
  @JsonProperty("tags")
  public List<String> tags;
  @JsonProperty("inner_models")
  public List<FakeInnerModel> innerModels;

  public FakeModel(
      String id, String name, int times, List<String> tags, List<FakeInnerModel> innerModels) {
    this.id = id;
    this.name = name;
    this.times = times;
    this.tags = tags;
    this.innerModels = innerModels;
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("times")
  public int getTimes() {
    return times;
  }

  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }

  public List<FakeInnerModel> getInnerModels() {
    return innerModels;
  }

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonPropertyOrder({
      "id",
      "name",
      "tags",
  })

  /**
   * This is purposefully missing methods so that we can validate no performance impact occurs.
   */
  public static class FakeInnerModel {
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("tags")
    public List<String> tags;

    public FakeInnerModel(String id, String name, List<String> tags) {
      this.id = id;
      this.name = name;
      this.tags = tags;
    }

    public List<String> getTags() {
      return tags;
    }
  }
}
