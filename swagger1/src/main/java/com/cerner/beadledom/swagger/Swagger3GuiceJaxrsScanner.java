package com.cerner.beadledom.swagger;

import com.google.inject.Injector;
import com.google.inject.Key;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiScanner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * @author John Leacox
 */
public class Swagger3GuiceJaxrsScanner implements OpenApiScanner {
  private final Injector injector;

  private OpenAPIConfiguration openAPIConfiguration;

  @Inject
  Swagger3GuiceJaxrsScanner(Injector injector) {
    this.injector = injector;
  }

  // TODO: Could we also set this through Guice?
  @Override
  public void setConfiguration(OpenAPIConfiguration openAPIConfiguration) {
    this.openAPIConfiguration = openAPIConfiguration;
  }

  @Override
  public Set<Class<?>> classes() {
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

  @Override
  public Map<String, Object> resources() {
    return new HashMap<>();
  }
}
