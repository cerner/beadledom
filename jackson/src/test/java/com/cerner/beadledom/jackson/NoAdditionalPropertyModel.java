package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Simple model with no additional_properties field
 */
public class NoAdditionalPropertyModel {

  @JsonProperty("field")
  public String field = null;
}
