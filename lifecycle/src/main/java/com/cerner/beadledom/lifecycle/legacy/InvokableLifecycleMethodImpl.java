package com.cerner.beadledom.lifecycle.legacy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link InvokableLifecycleMethod}.
 *
 * @author John Leacox
 * @since 1.2
 * @deprecated The Governator lifecycle implementation should be used instead if possible
 */
@Deprecated
class InvokableLifecycleMethodImpl implements InvokableLifecycleMethod {
  private static final Logger logger = LoggerFactory.getLogger(InvokableLifecycleMethodImpl.class);

  private final Object object;
  private final Method method;
  private final Class<? extends Annotation> annotation;

  InvokableLifecycleMethodImpl(
      Object object, Method method, Class<? extends Annotation> annotation) {
    this.object = object;
    this.method = method;
    this.annotation = annotation;
  }

  @Override
  public void invoke() {
    try {
      if (method.getParameterTypes().length > 0) {
        logger.warn(
            "Cannot execute object {}'s @{} lifecycle method {}"
                + " because it has unexpected parameters: skipping.",
            object.getClass().getSimpleName(), annotation.getSimpleName(), method.getName());
        return;
      }
      method.setAccessible(true);
      method.invoke(object);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    InvokableLifecycleMethodImpl that = (InvokableLifecycleMethodImpl) o;

    if (!object.equals(that.object)) {
      return false;
    }
    if (!method.equals(that.method)) {
      return false;
    }
    return annotation.equals(that.annotation);
  }

  @Override
  public int hashCode() {
    int result = object.hashCode();
    result = 31 * result + method.hashCode();
    result = 31 * result + annotation.hashCode();
    return result;
  }
}
