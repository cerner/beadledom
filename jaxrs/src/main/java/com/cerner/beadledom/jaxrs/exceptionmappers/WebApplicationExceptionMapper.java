package com.cerner.beadledom.jaxrs.exceptionmappers;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.cerner.beadledom.jaxrs.models.JsonError;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
 *
 * @author Cal Fisher
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  private static final Logger logger = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

  /**
   * Generates a Response object with a Status of the WebApplicationException, Content-Type
   * 'application/json', and a JsonError entity containing details about the unhandled exception in
   * Json format.
   *
   * @param exception the WebApplicationException that was not handled
   * @return a json response with the exception's status or 500 if the exception has no response
   */
  @Override
  public Response toResponse(WebApplicationException exception) {

    int statusCode;
    if (exception.getResponse() != null) {
      statusCode = exception.getResponse().getStatus();
    } else {
      statusCode = INTERNAL_SERVER_ERROR.getStatusCode();
    }

    if (statusCode >= 400 && statusCode < 500) {
      logger.warn("An unhandled WebApplicationException was thrown.", exception);
    } else if (statusCode >= 500) {
      logger.error("An unhandled WebApplicationException was thrown.", exception);
    }

    return Response
        .status(statusCode)
        .entity(createJsonError(statusCode))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private static JsonError createJsonError(int statusCode) {
    return JsonError.builder()
        .code(statusCode)
        .message(Response.Status.fromStatusCode(statusCode).getReasonPhrase())
        .build();
  }
}
