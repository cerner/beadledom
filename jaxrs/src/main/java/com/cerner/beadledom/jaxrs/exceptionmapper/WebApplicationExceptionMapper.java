package com.cerner.beadledom.jaxrs.exceptionmapper;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.cerner.beadledom.json.common.models.JsonError;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ExceptionMapper} for the {@link WebApplicationException} family of exceptions.
 *
 * <p>The intention of this exception mapper is to restructure the exceptions into a standard JSON
 * format.
 *
 * @author Cal Fisher
 * @since 2.6
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  private static final Logger logger = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

  /**
   * Maps an unhandled {@link WebApplicationException} to a {@link Response}.
   *
   * @param exception the {@link WebApplicationException} that was not handled.
   * @return a {@link Response} object with a status of the {@link WebApplicationException} or 500
   *     if the exception's response is null, content-type of 'application/json', and a
   *     {@link JsonError} entity containing details about the unhandled exception in JSON format.
   */
  @Override
  public Response toResponse(WebApplicationException exception) {

    Response response = exception.getResponse();
    int code = response == null ? INTERNAL_SERVER_ERROR.getStatusCode() : response.getStatus();
    Response.Status status = Response.Status.fromStatusCode(code);

    if (status == null) {
      code = INTERNAL_SERVER_ERROR.getStatusCode();
      status = INTERNAL_SERVER_ERROR;
    }

    if (code >= 400 && code < 500) {
      logger.warn("An unhandled exception was thrown.", exception);
    } else if (code >= 500) {
      logger.error("An unhandled exception was thrown.", exception);
    }

    if (response != null) {
      return Response
          .fromResponse(response)
          .status(status)
          .entity(
              JsonError.builder()
                  .code(code)
                  .message(status.getReasonPhrase())
                  .build())
          .type(MediaType.APPLICATION_JSON)
          .build();
    } else {
      return Response
          .status(status)
          .entity(
              JsonError.builder()
                  .code(code)
                  .message(status.getReasonPhrase())
                  .build())
          .type(MediaType.APPLICATION_JSON)
          .build();
    }
  }
}
