package com.cerner.beadledom.core;

import com.cerner.beadledom.avro.AvroJacksonGuiceModule;
import com.cerner.beadledom.avro.AvroSwaggerGuiceModule;
import com.cerner.beadledom.configuration.BeadledomConfigurationModule;
import com.cerner.beadledom.health.HealthModule;
import com.cerner.beadledom.jackson.JacksonModule;
import com.cerner.beadledom.jaxrs.JaxRsModule;
import com.cerner.beadledom.jaxrs.exceptionmapping.JsonMappingExceptionMapper;
import com.cerner.beadledom.jaxrs.exceptionmapping.JsonParseExceptionMapper;
import com.cerner.beadledom.jaxrs.exceptionmapping.ThrowableExceptionMapper;
import com.cerner.beadledom.jaxrs.exceptionmapping.WebApplicationExceptionMapper;
import com.cerner.beadledom.jaxrs.provider.FilteringJacksonJsonProvider;
import com.cerner.beadledom.stagemonitor.StagemonitorModule;
import com.cerner.beadledom.swagger.SwaggerModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * The core Beadledom module that installs and integrates all of the beadledom components.
 *
 * <p>This module is only dependent on the jax-rs API. When using beadledom with a jax-rs
 * implementation, use something like the {@code ResteasyModule} from beadledom-resteasy.
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link JsonParseExceptionMapper}</li>
 *     <li>{@link JsonMappingExceptionMapper}</li>
 *     <li>{@link WebApplicationExceptionMapper}</li>
 *     <li>{@link ThrowableExceptionMapper}</li>
 *     <li>{@link JacksonJsonProvider} with field filtering</li>
 * </ul>
 * @author John Leacox
 */
public class BeadledomModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new AvroJacksonGuiceModule());
    install(new AvroSwaggerGuiceModule());
    install(new BeadledomConfigurationModule());
    install(new HealthModule());
    install(new JacksonModule());
    install(new JaxRsModule());
    install(new StagemonitorModule());
    install(new SwaggerModule());

    bind(JsonParseExceptionMapper.class);
    bind(JsonMappingExceptionMapper.class);
    bind(WebApplicationExceptionMapper.class);
    bind(ThrowableExceptionMapper.class);
  }

  @Provides
  JacksonJsonProvider provideJacksonJsonProvider(ObjectMapper objectMapper) {
    return new FilteringJacksonJsonProvider(objectMapper);
  }
}
