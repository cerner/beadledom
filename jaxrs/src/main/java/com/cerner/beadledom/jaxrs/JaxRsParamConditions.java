package com.cerner.beadledom.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Simple static methods to be called at the start of jax-rs resource methods to verify correct
 * parameter conditions.
 *
 * <p>This allows constructs such as
 * <pre>
 *   {@code
 *     if (limit <= 0) {
 *       return Response
 *          .status(Response.Status.BAD_REQUEST)
 *          .entity("limit must be greater than or equal to 0")
 *          .build();
 *     }
 *   }
 * </pre>
 * to be replaced with the more compact
 * <pre>
 *   {@code
 *     checkParam(limit >= 0, "limit must be greater than or equal to 0");
 *   }
 * </pre>
 *
 * @author John Leacox
 * @author Nimesh Subramanian
 */
public class JaxRsParamConditions {
  private JaxRsParamConditions() {
  }

  /**
   * Ensures the truth of an expression involving a jax-rs parameter.
   *
   * @param expression a boolean expression
   * @param errorMessage the exception message to use if the check fails; will
   *     be converted to a string using {@link String#valueOf(Object)}
   * @throws WebApplicationException with response status 400 if {@code expression} is false
   */
  public static void checkParam(boolean expression, Object errorMessage) {
    if (!expression) {
      Response response = Response.status(Response.Status.BAD_REQUEST)
          .type(MediaType.TEXT_PLAIN)
          .build();
      throw new WebApplicationException(String.valueOf(errorMessage), response);
    }
  }
}
