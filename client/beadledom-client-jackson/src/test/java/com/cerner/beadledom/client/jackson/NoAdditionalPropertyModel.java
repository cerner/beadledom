package com.cerner.beadledom.client.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Simple model with no additional_properties field
 */
public class NoAdditionalPropertyModel {

  @JsonProperty("field")
  public String field = null;
}
