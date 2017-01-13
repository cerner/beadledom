package com.cerner.beadledom.resteasy;

import com.cerner.beadledom.jaxrs.GenericResponseBuilder;
import com.cerner.beadledom.jaxrs.GenericResponseBuilderFactory;
import java.lang.annotation.Annotation;

/**
 * An implementation of {@link GenericResponseBuilderFactory} for Resteasy.
 *
 * @author John Leacox
 * @since 1.3
 */
public class ResteasyGenericResponseBuilderFactory extends GenericResponseBuilderFactory {
  /**
   * Creates a new instance of {@link ResteasyGenericResponseBuilderFactory}.
   *
   * <p>This default constructor is required for use with {@link java.util.ServiceLoader}.
   */
  public ResteasyGenericResponseBuilderFactory() {
  }

  @Override
  public <T> GenericResponseBuilder<T> create(int status) {
    return ResteasyGenericResponseBuilder.create(status);
  }

  @Override
  public <T> GenericResponseBuilder<T> create(int status, T body) {
    return ResteasyGenericResponseBuilder.create(status, body);
  }

  @Override
  public <T> GenericResponseBuilder<T> create(int status, T body, Annotation[] annotations) {
    return ResteasyGenericResponseBuilder.create(status, body, annotations);
  }
}
