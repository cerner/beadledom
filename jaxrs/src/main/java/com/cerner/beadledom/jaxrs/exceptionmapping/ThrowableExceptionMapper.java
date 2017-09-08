package com.cerner.beadledom.jaxrs.exceptionmapping;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.cerner.beadledom.json.common.model.JsonError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ExceptionMapper} for the {@link Throwable} family of exceptions.
 *
 * <p>The intention of this exception mapper is to handle all unhandled exceptions thrown by a
 * service (i.e. exceptions that are not handled in try/catch blocks or do not have an
 * {@link ExceptionMapper} already implemented). The exceptions are restructured into a standard
 * JSON format.
 *
 * @author Brian van de Boogaard
 * @author Cal Fisher
 * @since 2.6
 */
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger logger = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

  /**
   * Maps an unhandled {@link Throwable} to a {@link Response}.
   *
   * @param exception the {@link Throwable} exception that was not handled
   * @return a {@link Response} object with a status of 500, content-type of 'application/json', and
   *     a {@link JsonError} entity containing details about the unhandled exception in JSON format
   */
  @Override
  public Response toResponse(Throwable exception) {

    logger.error("An unhandled exception was thrown.", exception);

    return Response
        .status(INTERNAL_SERVER_ERROR)
        .entity(
            JsonError.builder()
                .code(INTERNAL_SERVER_ERROR.getStatusCode())
                .message(INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build())
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
