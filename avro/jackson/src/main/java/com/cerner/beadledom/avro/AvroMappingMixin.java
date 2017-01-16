package com.cerner.beadledom.avro;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.avro.Schema;

/**
 * A Jackson mapping mixin to ignore the Avro {@code getSchema()} method.
 */
abstract class AvroMappingMixin {
  @JsonIgnore
  abstract Schema getSchema();
}
