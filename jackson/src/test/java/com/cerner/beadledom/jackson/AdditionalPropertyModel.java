package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

/**
 *  Simple model with a defined additional_properties field.
 */
@JsonPropertyOrder({
    "field",
    "additional_properties"
})
public class AdditionalPropertyModel {
  @JsonProperty("field")
  public String field = null;

  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperties(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
