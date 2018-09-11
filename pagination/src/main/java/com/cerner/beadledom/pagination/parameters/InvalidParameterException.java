package com.cerner.beadledom.pagination.parameters;

import com.cerner.beadledom.json.common.model.ErrorDetail;
import java.util.List;
import java.util.Objects;

/**
 * Exception used to communicate an invalid parameter.
 *
 * <p>Contains metadata used to build {@link Error} and {@link ErrorDetail} for the resulting
 * JSON response.
 *
 * @author Brian van de Boogaard
 * @since 3.1
 */
final class InvalidParameterException extends RuntimeException {
  private final String message;
  private final List<ErrorDetail> errorDetails;

  private InvalidParameterException(String message, List<ErrorDetail> errorDetails) {
    this.message = Objects.requireNonNull(message, "message: null");
    this.errorDetails = errorDetails;
  }

  public static InvalidParameterException create(String message) {
    return new InvalidParameterException(message, null);
  }

  public static InvalidParameterException create(String message, List<ErrorDetail> errorDetails) {
    return new InvalidParameterException(message, errorDetails);
  }

  public String getMessage() {
    return message;
  }

  public List<ErrorDetail> getErrorDetails() {
    return errorDetails;
  }
}
