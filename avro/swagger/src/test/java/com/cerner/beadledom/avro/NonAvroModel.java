package com.cerner.beadledom.avro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Sooo boring.")
public class NonAvroModel {

  @ApiModelProperty
  public String foo = "bar";
}
