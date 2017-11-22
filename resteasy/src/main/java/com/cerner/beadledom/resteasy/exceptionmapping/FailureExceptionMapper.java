package com.cerner.beadledom.resteasy.exceptionmapping;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.cerner.beadledom.json.common.model.JsonError;
import java.util.Optional;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ExceptionMapper} for the {@link Failure} family of exceptions.
 *
 * <p>{@link Failure} is a family of exceptions that Resteasy will throw when errors occur
 * internally within the framework. The intention of this exception mapper is to restructure the
 * exceptions into a standard JSON format.
 *
 * @see <a href="http://docs.jboss.org/resteasy/docs/3.1.4.Final/userguide/html_single/index.html#builtinException">Resteasy Built-in Internally-Thrown Exceptions</a>
 *
 * @author Cal Fisher
 * @since 2.6
 */
@Provider
public class FailureExceptionMapper implements ExceptionMapper<Failure> {

  private static final Logger logger = LoggerFactory.getLogger(FailureExceptionMapper.class);

  /**
   * Maps an unhandled {@link Failure} to a {@link Response}.
   *
   * @param exception the {@link Failure} exception that was not handled
   * @return a {@link Response} object with status code of the {@link Failure}'s response or the
   *     error code if the response is null (defaults to 500 if the exception's response is null
   *     and the error code is not a valid status code), a content-type of 'application/json', and
   *     a {@link JsonError} entity containing details about the unhandled exception in JSON format
   */
  @Override
  public Response toResponse(Failure exception) {

    int code = INTERNAL_SERVER_ERROR.getStatusCode();
    int errorCode = exception.getErrorCode();

    Response response = exception.getResponse();
    if (response != null) {
      code = response.getStatus();
    } else if (errorCode >= 100 && errorCode <= 599) {
      code = exception.getErrorCode();
    }

    if (code >= 400 && code < 500) {
      logger.warn("An unhandled exception was thrown.", exception);
    } else if (code >= 500) {
      logger.error("An unhandled exception was thrown.", exception);
    }

    return Optional.ofNullable(exception.getResponse())
        .map(Response::fromResponse)
        .orElse(Response.status(code))
        .entity(
            JsonError.builder()
                .code(code)
                .message(getMessage(code))
                .build())
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private String getMessage(int code) {
    Response.Status status = Response.Status.fromStatusCode(code);
    if (status != null) {
      return status.getReasonPhrase();
    }

    switch (Response.Status.Family.familyOf(code)) {
      case INFORMATIONAL:
        return "Informational";
      case SUCCESSFUL:
        return "Successful";
      case REDIRECTION:
        return "Redirection";
      case CLIENT_ERROR:
        return "Client Error";
      case SERVER_ERROR:
        return "Server Error";
      case OTHER:
      default:
        return "Unrecognized Status Code";
    }
  }
}
