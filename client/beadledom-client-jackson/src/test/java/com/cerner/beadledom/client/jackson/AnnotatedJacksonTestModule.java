package com.cerner.beadledom.client.jackson;

import com.cerner.beadledom.client.jackson.test.annotations.TestBindingAnnotation;
import com.cerner.beadledom.jackson.DeserializationFeatureFlag;
import com.cerner.beadledom.jackson.JsonGeneratorFeatureFlag;
import com.cerner.beadledom.jackson.JsonParserFeatureFlag;
import com.cerner.beadledom.jackson.MapperFeatureFlag;
import com.cerner.beadledom.jackson.SerializationFeatureFlag;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * A test Guice module to test the AnnotatedJacksonModule.
 */
public class AnnotatedJacksonTestModule extends AbstractModule {

  @Override
  protected void configure() {
    install(AnnotatedJacksonModule.with(TestBindingAnnotation.class));
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  Module getModule() {
    return new TestModule();
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  SerializationFeatureFlag getSerializationFeature() {
    return SerializationFeatureFlag.create(SerializationFeature.CLOSE_CLOSEABLE, true);
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  SerializationFeatureFlag getSerializationFeature2() {
    return SerializationFeatureFlag.create(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  SerializationFeatureFlag getSerializationFeature3() {
    return SerializationFeatureFlag
        .create(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  DeserializationFeatureFlag getDeserializationFeature() {
    return DeserializationFeatureFlag
        .create(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  MapperFeatureFlag getMapperFeature() {
    return MapperFeatureFlag.create(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  JsonGeneratorFeatureFlag getJsonGeneratorFeature() {
    return JsonGeneratorFeatureFlag.create(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
  }

  @ProvidesIntoSet
  @TestBindingAnnotation
  JsonParserFeatureFlag getJsonParserFeature() {
    return JsonParserFeatureFlag.create(JsonParser.Feature.ALLOW_COMMENTS, true);
  }
}
