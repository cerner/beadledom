package com.cerner.beadledom.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Mapper for {@link WebApplicationException}s that sets the {@code Content-Type} header and
 * entity body.
 *
 * <p>The default handling of {@link WebApplicationException}s, at least in Resteasy, does not
 * include the exception message or the {@code Content-Type} header. This mapper will set both of
 * the {@code Content-Type} and entity body if they are not set as well as log the exception
 * message.
 */
@Provider
public class PlainTextWebApplicationExceptionMapper
    implements ExceptionMapper<WebApplicationException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(
      PlainTextWebApplicationExceptionMapper.class);

  /**
   * Generates a Response object with Content-Type 'text/plain' and the exception message as the
   * entity body.
   *
   * <p>The Content-Type and entity body will only be set if they are not already set.
   */
  @Override
  public Response toResponse(WebApplicationException exception) {
    LOGGER.error("failed to execute", exception);

    if (exception.getResponse() == null) {
      return Response.serverError().build();
    }

    Response original = exception.getResponse();
    ResponseBuilder responseBuilder = Response.fromResponse(original);

    if (original.getEntity() == null) {
      responseBuilder.entity(exception.getMessage());
    }

    if (original.getMediaType() == null) {
      responseBuilder.type(MediaType.TEXT_PLAIN);
    }

    return responseBuilder.build();
  }
}
