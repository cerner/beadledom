package com.cerner.beadledom.pagination.parameters;

import com.cerner.beadledom.json.common.model.JsonError;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Base class to be used for JAX-RS method parameters.
 *
 * <p>Provides a hook for the validation of parameters as well as enforcing a consistent error
 * structure.
 *
 * @author Brian van de Boogaard
 * @author Will Pruyn
 * @since 3.1
 */
abstract class AbstractParameter<T> {
  private T value;
  private final String originalParameter;
  private final String parameterFieldName;

  /**
   * Constructor for instances of {@link AbstractParameter}.
   *
   * @param param the parameter to parse
   */
  public AbstractParameter(String param, String parameterFieldName) {
    this.originalParameter = param;
    this.parameterFieldName = parameterFieldName;
  }

  /**
   * Returns the parsed value.
   */
  public T getValue() {
    try {
      value = parse(originalParameter);
    } catch (InvalidParameterException e) {
      throw new WebApplicationException(onError(e));
    }
    return value;
  }

  /**
   * Returns the name of the parameter.
   */
  public String getParameterFieldName() {
    return parameterFieldName;
  }

  /**
   * Returns the original parameter.
   */
  public String getOriginalParameter() {
    return originalParameter;
  }

  /**
   * Returns the value wrapped in an {@link Optional}.
   */
  public Optional<T> asOptional() {
    return Optional.ofNullable(value);
  }

  /**
   * Returns true if the value is null; false otherwise.
   */
  public boolean isNull() {
    return value == null;
  }

  /**
   * Returns true if the value is non-null; false otherwise.
   */
  public boolean exists() {
    return !isNull();
  }

  /**
   * Convert the parameter from the request into type T.
   *
   * @param param the parameter to parse
   * @return converted parameter
   * @throws InvalidParameterException is thrown if parsing fails
   */
  protected abstract T parse(String param) throws InvalidParameterException;

  protected Response onError(InvalidParameterException e) {
    return Response.status(
        Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON)
        .entity(getError(e))
        .build();
  }

  protected JsonError getError(InvalidParameterException e) {
    return JsonError.builder()
        .code(Status.BAD_REQUEST.getStatusCode())
        .message(e.getMessage())
        .errors(e.getErrorDetails())
        .build();
  }
}
