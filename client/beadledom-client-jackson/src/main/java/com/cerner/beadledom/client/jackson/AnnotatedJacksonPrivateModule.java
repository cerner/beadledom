package com.cerner.beadledom.client.jackson;

import com.cerner.beadledom.guice.BindingAnnotations;
import com.cerner.beadledom.guice.dynamicbindings.AnnotatedModule;
import com.cerner.beadledom.guice.dynamicbindings.DynamicBindingProvider;
import com.cerner.beadledom.jackson.DeserializationFeatureFlag;
import com.cerner.beadledom.jackson.JsonGeneratorFeatureFlag;
import com.cerner.beadledom.jackson.JsonParserFeatureFlag;
import com.cerner.beadledom.jackson.MapperFeatureFlag;
import com.cerner.beadledom.jackson.SerializationFeatureFlag;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.BindingAnnotation;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A private Guice module that provides the Json Serialization via {@link ObjectMapper}.
 *
 * <p>This module is intended to be used within the {@link AnnotatedJacksonModule}.
 *
 * <p>This module exposes the following
 * <ul>
 *   <li>
 *     {@link JacksonJsonProvider} annotated with the given {@link BindingAnnotation} via a guice
 *     provider {@link JacksonJsonGuiceProvider}
 *   </li>
 * </ul>
 *
 * <p>This module binds the following
 * <ul>
 *    <li>A {@link DynamicBindingProvider} for a Set of {@link Module}</li>
 *    <li>A {@link DynamicBindingProvider} for a Set of {@link SerializationFeatureFlag}</li>
 *    <li>A {@link DynamicBindingProvider} for a Set of {@link DeserializationFeatureFlag}</li>
 *    <li>A {@link DynamicBindingProvider} for a Set of {@link JsonGeneratorFeatureFlag}</li>
 *    <li>A {@link DynamicBindingProvider} for a Set of {@link JsonParserFeatureFlag}</li>
 *    <li>A {@link DynamicBindingProvider} for a Set of {@link MapperFeatureFlag}</li>
 * </ul>
 *
 * @author Sundeep Paruvu
 * @since 2.2
 * @deprecated As of 3.6, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
class AnnotatedJacksonPrivateModule extends AnnotatedModule {

  protected AnnotatedJacksonPrivateModule(
      Class<? extends Annotation> clientBindingAnnotation) {
    super(clientBindingAnnotation);
  }

  /**
   * A static method to create an instance of {@link AnnotatedJacksonPrivateModule}.
   *
   * @param clientBindingAnnotation a {@link BindingAnnotation} to which the {@link ObjectMapper}
   *     need to be annotated with.
   *
   * @return an instance of {@link AnnotatedJacksonPrivateModule}
   */
  static AnnotatedJacksonPrivateModule with(
      Class<? extends Annotation> clientBindingAnnotation) {
    if (clientBindingAnnotation == null) {
      throw new NullPointerException("clientBindingAnnotation:null");
    }

    BindingAnnotations.checkIsBindingAnnotation(clientBindingAnnotation);

    return new AnnotatedJacksonPrivateModule(clientBindingAnnotation);
  }

  @Override
  protected void configure() {
    AnnotatedObjectMapperProvider provider =
        new AnnotatedObjectMapperProvider(getBindingAnnotation());

    bind(ObjectMapper.class).annotatedWith(getBindingAnnotation()).toProvider(provider)
        .asEagerSingleton();

    bindDynamicProvider(ObjectMapper.class);

    expose(ObjectMapper.class).annotatedWith(getBindingAnnotation());

    bind(JacksonJsonProvider.class).annotatedWith(getBindingAnnotation())
        .toProvider(new JacksonJsonGuiceProvider(getBindingAnnotation()));

    expose(JacksonJsonProvider.class).annotatedWith(getBindingAnnotation());

    bindDynamicProvider(new TypeLiteral<Set<Module>>() {
    });

    bindDynamicProvider(new TypeLiteral<Set<SerializationFeatureFlag>>() {
    });

    bindDynamicProvider(new TypeLiteral<Set<DeserializationFeatureFlag>>() {
    });

    bindDynamicProvider(new TypeLiteral<Set<JsonGeneratorFeatureFlag>>() {
    });

    bindDynamicProvider(new TypeLiteral<Set<JsonParserFeatureFlag>>() {
    });

    bindDynamicProvider(new TypeLiteral<Set<MapperFeatureFlag>>() {
    });
  }
}
