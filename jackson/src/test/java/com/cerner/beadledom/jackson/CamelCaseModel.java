package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A JSON model with a camel case field for testing.
 *
 * @author John Leacox
 */
public class CamelCaseModel {
  @JsonProperty
  public String longFieldNameWithCamelCase = "test";
}
