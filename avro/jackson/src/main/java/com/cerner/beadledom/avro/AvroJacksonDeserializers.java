package com.cerner.beadledom.avro;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificRecordBuilderBase;

/**
 * Jackson deserializers for Avro {@link SpecificRecordBase} types.
 *
 * <p>This class ensures that Jackson deserializes Avro class by using the generated builder classes
 * rather than by directly instantiating the generated value classes. This ensures that any fields
 * not specified in the input JSON will have their default values (if any) applied, rather than
 * being left null. It only works if there is a static nested class named @{code Builder} inside
 * the value class, with a {@code build} method; the default Avro generator templates produce these.
 */
public class AvroJacksonDeserializers extends SimpleDeserializers {
  @Override
  public JsonDeserializer<?> findBeanDeserializer(
      final JavaType type, DeserializationConfig config, BeanDescription beanDesc)
      throws JsonMappingException {
    if (!SpecificRecordBase.class.isAssignableFrom(type.getRawClass())) {
      return null;
    }

    @SuppressWarnings("unchecked")
    final Class<? extends SpecificRecordBase> avroClass =
        (Class<? extends SpecificRecordBase>) type.getRawClass();
    final Class<? extends SpecificRecordBuilderBase> builderClass =
        getAvroBuilderClass(avroClass);
    if (builderClass == null) {
      return null;
    }

    return new StdDelegatingDeserializer<>(
        new Converter<Object, Object>() {
          @Override
          public Object convert(Object value) {
            if (!builderClass.isInstance(value)) {
              throw new IllegalStateException(
                  "Converter for " + avroClass + " received object of class " + value.getClass());
            }
            return ((SpecificRecordBuilderBase) value).build();
          }

          @Override
          public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(builderClass);
          }

          @Override
          public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(avroClass);
          }
        }
    );
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends SpecificRecordBuilderBase> getAvroBuilderClass(
      Class<? extends SpecificRecordBase> avroClass) {
    for (Class<?> clazz : avroClass.getClasses()) {
      if (SpecificRecordBuilderBase.class.isAssignableFrom(clazz)) {
        try {
          if (clazz.getMethod("build").getReturnType() != avroClass) {
            continue;
          }
        } catch (NoSuchMethodException expected) {
        }
        return (Class<? extends SpecificRecordBuilderBase>) clazz;
      }
    }
    return null;
  }
}
