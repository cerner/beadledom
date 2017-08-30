package com.cerner.beadledom.jaxrs.exceptionmappers;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.cerner.beadledom.jaxrs.models.JsonError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception Mapper for all unhandled Exceptions thrown by the service.
 *
 * @author Brian van de Boogaard
 * @author Cal Fisher
 */
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger logger = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

  /**
   * Generates a Response object with a Status of 500, Content-Type 'application/json', and a
   * JsonError entity containing details about the unhandled exception in Json format.
   *
   * @param exception the Throwable exception that was not handled
   * @return a json response with status 500
   */
  @Override
  public Response toResponse(Throwable exception) {

    logger.error("Unhandled Exception Occurred.", exception);

    return Response
        .status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(createJsonError())
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private static JsonError createJsonError() {
    return JsonError.builder()
        .code(INTERNAL_SERVER_ERROR.getStatusCode())
        .message(INTERNAL_SERVER_ERROR.getReasonPhrase())
        .build();
  }
}
