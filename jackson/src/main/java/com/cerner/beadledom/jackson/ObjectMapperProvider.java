package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.util.Set;

/**
 * A Guice provider for {@link ObjectMapper}.
 *
 * @author Nimesh Subramanian
 */
class ObjectMapperProvider implements Provider<ObjectMapper> {

  private  Set<Module> jacksonModules;
  private  Set<SerializationFeatureFlag> serializationFeatureFlag;
  private  Set<DeserializationFeatureFlag> deserializationFeatureFlag;
  private  Set<MapperFeatureFlag> mapperFeatureFlag;
  private  Set<JsonGeneratorFeatureFlag> jsonGeneratorFeatureFlag;
  private  Set<JsonParserFeatureFlag> jsonParserFeatureFlag;

  @Inject
  ObjectMapperProvider(Set<Module> jacksonModules,
      Set<SerializationFeatureFlag> serializationFeatureFlag,
      Set<DeserializationFeatureFlag> deserializationFeatureFlag,
      Set<MapperFeatureFlag> mapperFeatureFlag,
      Set<JsonGeneratorFeatureFlag> jsonGeneratorFeatureFlag,
      Set<JsonParserFeatureFlag> jsonParserFeatureFlag) {
    this.serializationFeatureFlag = serializationFeatureFlag;
    this.deserializationFeatureFlag = deserializationFeatureFlag;
    this.mapperFeatureFlag = mapperFeatureFlag;
    this.jsonGeneratorFeatureFlag = jsonGeneratorFeatureFlag;
    this.jsonParserFeatureFlag = jsonParserFeatureFlag;
    this.jacksonModules = jacksonModules;
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
    
    for (SerializationFeatureFlag feature: serializationFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (DeserializationFeatureFlag feature: deserializationFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (MapperFeatureFlag feature: mapperFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (JsonParserFeatureFlag feature: jsonParserFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    for (JsonGeneratorFeatureFlag feature: jsonGeneratorFeatureFlag) {
      objectMapper.configure(feature.feature(), feature.isEnabled());
    }

    return objectMapper;
  }
}

