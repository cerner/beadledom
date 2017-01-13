package com.cerner.beadledom.jaxrs;

import java.lang.annotation.Annotation;
import java.util.ServiceLoader;
import javax.ws.rs.core.Response;

/**
 * A {@link GenericResponseBuilderFactory} implementation used for testing.
 *
 * <p>Since {@link GenericResponses} uses {@link ServiceLoader} we need a test implementation for
 * testing.
 *
 * @author John Leacox
 */
public class TestingGenericResponseBuilderFactory extends GenericResponseBuilderFactory {
  @Override
  public <T> GenericResponseBuilder<T> create(int status) {
    return new TestingGenericResponseBuilder<T>(status, null);
  }

  @Override
  public <T> GenericResponseBuilder<T> create(int status, T body) {
    return new TestingGenericResponseBuilder<T>(status, body);
  }

  @Override
  public <T> GenericResponseBuilder<T> create(
      int status, T body, Annotation[] annotations) {
    return new TestingGenericResponseBuilder<T>(status, body, annotations);
  }

  private static class TestingGenericResponseBuilder<T> extends GenericResponseBuilder<T> {
    TestingGenericResponseBuilder(int status, T body) {
      super(Response.status(status));
      this.entity(body);
    }

    TestingGenericResponseBuilder(int status, T body, Annotation[] annotations) {
      super(Response.status(status));
      this.entity(body, annotations);
    }

    @Override
    protected GenericResponse<T> build(T body, Response rawResponse) {
      return DelegatingGenericResponse.create(body, rawResponse);
    }
  }
}
