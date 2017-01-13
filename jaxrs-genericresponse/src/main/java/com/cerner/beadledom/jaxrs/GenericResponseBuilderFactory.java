package com.cerner.beadledom.jaxrs;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * A factory for creating an instance of a {@link GenericResponseBuilder}.
 *
 * <p>This class is not meant to be consumed directly. Instead the {@link GenericResponses} class
 * should be used for obtaining an instance of a builder. This factory exists as a helper to
 * {@link GenericResponses} and allows it to obtain the correct builder implementation class.
 *
 * @author John Leacox
 * @since 1.3
 */
public abstract class GenericResponseBuilderFactory {
  /**
   * Creates a new instance of {@link GenericResponseBuilderFactory} using {@link ServiceLoader}.
   */
  public static GenericResponseBuilderFactory newInstance() {
    ServiceLoader<GenericResponseBuilderFactory> clientBuilderFactoryLoader =
        ServiceLoader.load(GenericResponseBuilderFactory.class);

    Iterator<GenericResponseBuilderFactory> iterator = clientBuilderFactoryLoader.iterator();

    if (!iterator.hasNext()) {
      throw new IllegalStateException(
          "An implementation of GenericResponseBuilderFactory was not found");
    }

    return iterator.next();
  }

  /**
   * Creates a new instance of {@link GenericResponseBuilder} with the given status.
   */
  public abstract <T> GenericResponseBuilder<T> create(int status);

  /**
   * Creates a new instance of {@link GenericResponseBuilder} with the given status and entity
   * body.
   */
  public abstract <T> GenericResponseBuilder<T> create(int status, T body);

  /**
   * Creates a new instance of {@link GenericResponseBuilder}.
   *
   * @param status the status for the new response builder
   * @param body the entity body for the new response builder
   * @param annotations annotations, in addition to the annotations on the resource method
   *     returning the response, to be passed to the {@link MessageBodyWriter}
   * @param <T> the type of the entity body
   */
  public abstract <T> GenericResponseBuilder<T> create(
      int status, T body, Annotation[] annotations);
}
