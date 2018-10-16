package com.cerner.beadledom.swagger2;

import com.google.inject.Injector;
import com.google.inject.Key;
import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.DefaultJaxrsScanner;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

/**
 * Finds JAX-RS resource classes via Guice, so that they can be scanned for Swagger API annotations.
 *
 * <p>Like RESTEasy, this simply searches the injector for bindings that have a {@link Path}
 * annotation.
 */
public class SwaggerGuiceJaxrsScanner extends DefaultJaxrsScanner implements SwaggerConfig {
  private final Injector injector;
  private final Info info;

  @Inject
  SwaggerGuiceJaxrsScanner(Injector injector, Info info) {
    this.injector = injector;
    this.info = info;
  }

  @Override
  public Set<Class<?>> classesFromContext(Application app, ServletConfig sc) {
    Set<Class<?>> classes = new HashSet<>();
    addJaxrsClasses(injector, classes);
    return classes;
  }

  private void addJaxrsClasses(Injector injector, Set<Class<?>> classes) {
    for (Key<?> key : injector.getBindings().keySet()) {
      Class<?> clazz = key.getTypeLiteral().getRawType();
      if (hasPathAnnotation(clazz)) {
        classes.add(clazz);
      }
    }

    Injector tempInjector = injector;
    while (tempInjector.getParent() != null) {
      tempInjector = tempInjector.getParent();
      addJaxrsClasses(tempInjector, classes);
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

  @Override
  public Swagger configure(Swagger swagger) {
    return swagger.info(info);
  }

  @Override
  public String getFilterClass() {
    return null;
  }
}
