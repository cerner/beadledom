package com.cerner.beadledom.swagger;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import scala.collection.JavaConverters;

/**
 * Finds JAX-RS resource classes via Guice, so that they can be scanned for Swagger API annotations.
 *
 * <p>Like RESTEasy, this simply searches the injector for bindings that have a {@link Path}
 * annotation.
 */
public class SwaggerGuiceJaxrsScanner extends DefaultJaxrsScanner {
  private final Injector injector;

  @Inject
  SwaggerGuiceJaxrsScanner(Injector injector) {
    this.injector = injector;
  }

  @Override
  public scala.collection.immutable.List<Class<?>> classesFromContext(
      Application app, ServletConfig sc) {
    List<Class<?>> classes = Lists.newArrayList();
    addJaxrsClasses(injector, classes);
    return JavaConverters.asScalaBufferConverter(classes).asScala().toList();
  }

  private void addJaxrsClasses(Injector injector, List<Class<?>> classes) {
    for (Key<?> key : injector.getBindings().keySet()) {
      Class<?> clazz = key.getTypeLiteral().getRawType();
      if (hasPathAnnotation(clazz)) {
        classes.add(clazz);
      }
    }

    while (injector.getParent() != null) {
      injector = injector.getParent();
      addJaxrsClasses(injector, classes);
    }
  }

  private boolean hasPathAnnotation(Class clazz) {
    if (clazz.isAnnotationPresent(Path.class)) {
      return true;
    }

    if (Arrays.stream(clazz.getInterfaces()).anyMatch(this::hasPathAnnotation)) {
      return true;
    }

    Class superClass = clazz.getSuperclass();
    if (superClass != Object.class && superClass != null) {
      return hasPathAnnotation(clazz.getSuperclass());
    }

    return false;
  }
}
