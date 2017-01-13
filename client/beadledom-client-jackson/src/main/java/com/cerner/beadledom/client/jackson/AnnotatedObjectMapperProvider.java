package com.cerner.beadledom.client.jackson;

import com.cerner.beadledom.guice.dynamicbindings.DynamicBindingProvider;
import com.cerner.beadledom.jackson.DeserializationFeatureFlag;
import com.cerner.beadledom.jackson.JsonGeneratorFeatureFlag;
import com.cerner.beadledom.jackson.JsonParserFeatureFlag;
import com.cerner.beadledom.jackson.MapperFeatureFlag;
import com.cerner.beadledom.jackson.SerializationFeatureFlag;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A Guice provider for {@link ObjectMapper}.
 *
 * <p>This provider uses the given {@link BindingAnnotation} to retrieve the configuration from the
 * Guice injected {@link DynamicBindingProvider}s to build the {@link ObjectMapper}.
 *
 * @author Sundeep Paruvu
 * @since 2.2
 */
class AnnotatedObjectMapperProvider implements Provider<ObjectMapper> {

  private final Class<? extends Annotation> clientBindingAnnotation;

  private Set<Module> jacksonModules;
  private Set<SerializationFeatureFlag> serializationFeatureFlag;
  private Set<DeserializationFeatureFlag> deserializationFeatureFlag;
  private Set<MapperFeatureFlag> mapperFeatureFlag;
  private Set<JsonGeneratorFeatureFlag> jsonGeneratorFeatureFlag;
  private Set<JsonParserFeatureFlag> jsonParserFeatureFlag;

  AnnotatedObjectMapperProvider(Class<? extends Annotation> clientBindingAnnotation) {
    this.clientBindingAnnotation = clientBindingAnnotation;
  }

  @Inject
  void init(
      DynamicBindingProvider<Set<Module>> jacksonModules,
      DynamicBindingProvider<Set<SerializationFeatureFlag>> serializationFeatureFlag,
      DynamicBindingProvider<Set<DeserializationFeatureFlag>> deserializationFeatureFlag,
      DynamicBindingProvider<Set<MapperFeatureFlag>> mapperFeatureFlag,
      DynamicBindingProvider<Set<JsonGeneratorFeatureFlag>> jsonGeneratorFeatureFlag,
      DynamicBindingProvider<Set<JsonParserFeatureFlag>> jsonParserFeatureFlag) {
    this.jacksonModules = jacksonModules.get(clientBindingAnnotation);
    this.serializationFeatureFlag = serializationFeatureFlag.get(clientBindingAnnotation);
    this.deserializationFeatureFlag = deserializationFeatureFlag.get(clientBindingAnnotation);
    this.mapperFeatureFlag = mapperFeatureFlag.get(clientBindingAnnotation);
    this.jsonGeneratorFeatureFlag = jsonGeneratorFeatureFlag.get(clientBindingAnnotation);
    this.jsonParserFeatureFlag = jsonParserFeatureFlag.get(clientBindingAnnotation);
  }

  @Override
  public ObjectMapper get() {
    ObjectMapper objectMapper = new ObjectMapper();

    // Sets default values before looking at bound features
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    objectMapper.setPropertyNamingStrategy(
        PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    objectMapper.registerModules(jacksonModules);

    for (SerializationFeatureFlag feature : serializationFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (DeserializationFeatureFlag feature : deserializationFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (MapperFeatureFlag feature : mapperFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (JsonParserFeatureFlag feature : jsonParserFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (JsonGeneratorFeatureFlag feature : jsonGeneratorFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    return objectMapper;
  }
}
