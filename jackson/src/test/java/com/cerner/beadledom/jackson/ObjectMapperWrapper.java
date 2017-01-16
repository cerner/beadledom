package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * A wrapper around {@link ObjectMapper} that allows retrieving the registered modules for testing.
 *
 * @author John Leacox
 */
public class ObjectMapperWrapper {
  private final ObjectMapper wrapped;

  ObjectMapperWrapper(ObjectMapper wrapped) {
    this.wrapped = wrapped;
  }

  Set<Object> getRegisteredModules() {
    try {
      Class clazz = wrapped.getClass();
      Field moduleTypesField = clazz.getDeclaredField("_registeredModuleTypes");
      moduleTypesField.setAccessible(true);
      return (Set<Object>) moduleTypesField.get(wrapped);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
