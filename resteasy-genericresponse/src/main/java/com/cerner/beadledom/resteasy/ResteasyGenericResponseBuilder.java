package com.cerner.beadledom.resteasy;

import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.jaxrs.GenericResponseBuilder;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.Response;

/**
 * An implementation of {@link GenericResponseBuilder} for Resteasy.
 *
 * @author John Leacox
 * @since 1.3
 */
class ResteasyGenericResponseBuilder<T> extends GenericResponseBuilder<T> {

  private ResteasyGenericResponseBuilder(int status, T body) {
    super(Response.status(status));
    if (body != null) {
      this.entity(body);
    }
  }

  private ResteasyGenericResponseBuilder(int status, T body, Annotation[] annotations) {
    super(Response.status(status));
    if (body != null) {
      this.entity(body, annotations);
    }
  }

  static <T> ResteasyGenericResponseBuilder<T> create(int status) {
    return new ResteasyGenericResponseBuilder<T>(status, null);
  }

  static <T> ResteasyGenericResponseBuilder<T> create(int status, T body) {
    return new ResteasyGenericResponseBuilder<T>(status, body);
  }

  static <T> ResteasyGenericResponseBuilder<T> create(
      int status, T body, Annotation[] annotations) {
    return new ResteasyGenericResponseBuilder<T>(status, body, annotations);
  }

  @Override
  protected GenericResponse<T> build(T body, Response rawResponse) {
    return BuiltGenericResponse.create(body, rawResponse);
  }
}
