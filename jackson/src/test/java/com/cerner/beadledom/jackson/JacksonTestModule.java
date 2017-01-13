package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * A Jackson Module for tests
 *
 * @author Nimesh Subramanian
 */

public class JacksonTestModule extends AbstractModule {

  @Override
  protected void configure() {

    Multibinder<Module> jacksonModuleBinder = Multibinder.newSetBinder(binder(), Module.class);
    jacksonModuleBinder.addBinding().to(TestModule.class);
    install(new JacksonModule());
  }

  @ProvidesIntoSet
  SerializationFeatureFlag getSerializationFeature() {
    return SerializationFeatureFlag.create(SerializationFeature.CLOSE_CLOSEABLE, true);
  }

  @ProvidesIntoSet
  SerializationFeatureFlag getSerializationFeature2() {
    return SerializationFeatureFlag.create(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
  }

  @ProvidesIntoSet
  SerializationFeatureFlag getSerializationFeature3() {
    return SerializationFeatureFlag.create(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
  }

  @ProvidesIntoSet
  DeserializationFeatureFlag getDeserializationFeature() {
    return DeserializationFeatureFlag.create(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
  }

  @ProvidesIntoSet
  MapperFeatureFlag getMapperFeature() {
    return MapperFeatureFlag.create(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
  }

  @ProvidesIntoSet
  JsonGeneratorFeatureFlag getJsonGeneratorFeature() {
    return JsonGeneratorFeatureFlag.create(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
  }

  @ProvidesIntoSet
  JsonParserFeatureFlag getJsonParserFeature() {
    return JsonParserFeatureFlag.create(JsonParser.Feature.ALLOW_COMMENTS, true);
  }
}

