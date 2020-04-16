package com.cerner.beadledom.client;

import com.cerner.beadledom.jaxrs.JaxRsModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

public class FauxModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new JaxRsModule());
    install(new RequestScopeModule());
    bind(TestResource.class).to(TestResourceImpl.class);
  }

  @Provides
  @Singleton
  ObjectMapper provideObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.setPropertyNamingStrategy(
        PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    return objectMapper;
  }

  @Provides
  @Singleton
  JacksonJsonProvider provideJacksonJsonProvider(ObjectMapper objectMapper) {
    return new JacksonJsonProvider(objectMapper);
  }

  @Provides
  @Singleton
  TestingExceptionMapper provideExceptionMapper() {
    return new TestingExceptionMapper();
  }

  @Provider
  static class TestingExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
      return Response
          .fromResponse(exception.getResponse())
          .entity(exception.getMessage())
          .type(MediaType.APPLICATION_JSON)
          .build();
    }
  }
}
