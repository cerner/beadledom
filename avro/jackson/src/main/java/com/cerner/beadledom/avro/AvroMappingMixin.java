package com.cerner.beadledom.avro;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificData;

/**
 * A Jackson mapping mixin to ignore the Avro {@code getSchema()} and {@code getSpecificData()}
 * methods.
 */
abstract class AvroMappingMixin {
  @JsonIgnore
  abstract Schema getSchema();

  @JsonIgnore
  abstract SpecificData getSpecificData();
}
