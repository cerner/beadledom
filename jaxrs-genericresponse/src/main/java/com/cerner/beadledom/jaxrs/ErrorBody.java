package com.cerner.beadledom.jaxrs;

import javax.ws.rs.core.Response;

/**
 * A one-time stream of the response entity body representing an error.
 *
 * <p>Like the {@link Response#readEntity} methods this provides one-time use access to the body.
 * Once the body has been consumed by one of the methods on this class, additional calls to any of
 * the methods will lead to an exception.
 *
 * @author John Leacox
 * @since 1.3
 */
public class ErrorBody {
  private final Response response;

  private ErrorBody(Response response) {
    this.response = response;
  }

  /**
   * Creates a new instance of {@link ErrorBody} from the response.
   */
  public static ErrorBody fromResponse(Response response) {
    if (response == null) {
      throw new NullPointerException("response:null");
    }

    return new ErrorBody(response);
  }

  /**
   * Gets the error body as a string.
   */
  public final String string() {
    return response.readEntity(String.class);
  }
}
