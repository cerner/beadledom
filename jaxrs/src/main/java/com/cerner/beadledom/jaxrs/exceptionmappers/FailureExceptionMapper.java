package com.cerner.beadledom.jaxrs.exceptionmappers;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.cerner.beadledom.jaxrs.models.JsonError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception Mapper for all unhandled Exceptions extending from the {@link Failure} hierarchy
 * (specifically for Resteasy Built-in Internally-Thrown Exceptions like {@link ReaderException},
 * {@link WriterException}, etc.) thrown by the service. See
 * http://docs.jboss.org/resteasy/docs/3.1.4.Final/userguide/html_single/index.html#builtinException
 * for more information.
 *
 * @author Cal Fisher
 */
@Provider
public class FailureExceptionMapper implements ExceptionMapper<Failure> {

  private static final Logger logger = LoggerFactory.getLogger(FailureExceptionMapper.class);

  /**
   * Generates a Response object with a Status of the Failure, Content-Type 'application/json', and
   * a JsonError entity containing details about the unhandled exception in Json format.
   *
   * @param exception the Failure exception that was not handled
   * @return a json response with the exception's status or 500 if the exception has no response
   */
  @Override
  public Response toResponse(Failure exception) {

    logger.error("An unhandled Failure exception was thrown.", exception);

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
