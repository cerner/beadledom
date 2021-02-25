package com.cerner.beadledom.jaxrs.provider;

import com.cerner.beadledom.jackson.filter.FieldFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * An extension of {@link JacksonJsonProvider} that provides filtering of JSON fields using
 * {@link FieldFilter}.
 *
 * <p>This will return JSON containing only the fields specified in the 'fields' query parameter. if
 * the 'fields' query parameter is not specified, then all fields will be returned.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class FilteringJacksonJsonProvider extends JacksonJsonProvider {
  private final ObjectMapper objectMapper;
  @Context
  UriInfo uriInfo;

  @Context
  HttpServletResponse httpServletResponse;

  /**
   * Creates a new instance of {@link FilteringJacksonJsonProvider}.
   */
  @Inject
  public FilteringJacksonJsonProvider(ObjectMapper objectMapper) {
    super(objectMapper);
    this.objectMapper = objectMapper;
  }

  @Override
  public long getSize(
      Object object, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(
      Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream os) throws IOException {
        
    if (httpServletResponse.getStatus() >= 400 ) {
      super.writeTo(o, type, genericType, annotations, mediaType, httpHeaders, os);
      return;
    }

    String fields = uriInfo.getQueryParameters() == null ? null
        : uriInfo.getQueryParameters().getFirst("fields");

    FieldFilter fieldFilter = FieldFilter.create(fields);
    if (!fieldFilter.hasFilters()) {
      super.writeTo(o, type, genericType, annotations, mediaType, httpHeaders, os);
      return;
    }

    JsonGenerator jgen = objectMapper.getFactory().createGenerator(os);
    TokenBuffer tokenBuffer = new TokenBuffer(objectMapper, false);
    objectMapper.writeValue(tokenBuffer, o);
    JsonParser jsonParser = tokenBuffer.asParser();
    fieldFilter.writeJson(jsonParser, jgen);
    jgen.flush();
  }
}
