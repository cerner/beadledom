package com.cerner.beadledom.avro;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.collect.Lists;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;
import io.swagger.models.properties.StringProperty;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This produces Swagger model schemas for Avro generated classes, by looking at the Avro schema
 * (rather than by inspecting the Java class for members and annotations).
 *
 * <p>This will only attempt to process classes which derive from
 * {@link SpecificRecordBase} and contain a static field named SCHEMA$
 * which is an instance of {@link Schema}. The default Avro code generator for Java
 * produces such classes. If you need to handle other classes, override {@link #getSchema(Class)}.
 *
 * <p>All of the following are currently unsupported and will result in omitted fields:
 * <ul>
 *     <li>schema type 'fixed'</li>
 *     <li>maps</li>
 *     <li>unions, except unions of 'null' with one other type</li>
 *     <li>nested collections such as {@code array<array<string>>} are unsupported, you can of
 *     course have a collection inside a record inside a collection</li>
 * </ul>
 *
 * <p>Unions of 'null' and one other type will be treated as optional fields; all other fields will
 * be assumed required.
 *
 * <p>Avro fields of type 'bytes' are represented as Swagger properties of type 'string', format
 * 'byte'.
 *
 * <p>By default, field names are converted to snake case. You can override the
 * {@link #getFieldName(Schema.Field)} method to change this.
 *
 * <p>To accommodate javadoc-style comments in avdl files, by default, if a line of a model/field
 * description begins with whitespace followed by an asterisk, those characters (along with one
 * subsequent whitespace character) will be removed. You can override the
 * {@link #adjustDescription(String)} method to change this.
 */
public class SwaggerAvroModelConverter implements ModelConverter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerAvroModelConverter.class);

  @Override
  public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
    if (!(type instanceof Class<?>)) {
      return chain.next().resolve(type, context, chain);
    }

    Class<?> clazz = (Class<?>) type;

    Schema schema = getSchema(clazz);
    if (schema == null) {
      return chain.next().resolve(type, context, chain);
    }

    ModelImpl model = new ModelImpl()
            .name(getName(schema))
            .description(adjustDescription(schema.getDoc()));

    for (Schema.Field field : schema.getFields()) {
      Property property = parseField(field);
      if (property == null) {
        LOGGER.debug("Omitted field {} of schema {} from swagger docs", field.name(), schema.getName());
        continue;
      }
      model.addProperty(getFieldName(field), property);
    }

    return model;
  }

  @Override
  public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {
    if (!(type instanceof Schema.Field)) {
      return chain.next().resolveProperty(type, context, annotations, chain);
    }

    Schema.Field field = (Schema.Field) type;

    return parseField(field);
  }

  protected String getName(Schema schema) {
    return schema.getName();
  }

  /**
   * Generate a ModelProperty for the given schema.
   *
   * <p>This is used by {@link #parseField(Schema.Field)}, which is responsible for
   * overriding the parts of the ModelProperty (such as position and description) that cannot be
   * determined merely by looking at the schema. It may also be used recursively to build collection
   * and union types.
   *
   * @return the parsed property, or null if it cannot/should not be represented in the Swagger model
   */
  @Nullable
  protected Property parseSchema(Schema schema) {
    switch (schema.getType()) {
      case RECORD:
        Property record = PropertyBuilder.build("object", getName(schema), Collections.emptyMap());
        record.setRequired(true);
        return record;
      case ENUM:
        // TODO may wish to include the enum's documentation in the field documentation, since it
        // won't appear anywhere else
        StringProperty property = new StringProperty();
        property.required(true);
        property.setEnum(schema.getEnumSymbols());

        return property;
      case ARRAY:
        Property elementsProperty = parseSchema(schema.getElementType());
        if (elementsProperty == null) {
          return null;
        }

        if (elementsProperty instanceof ArrayProperty) {
          LOGGER.debug("Cannot include nested collection schema in swagger docs: {}", schema);
          return null;
        }

        Property arrayProperty = new ArrayProperty(elementsProperty);
        arrayProperty.setRequired(true);

        return arrayProperty;
      case BOOLEAN:
        Property booleanProperty = new BooleanProperty();
        booleanProperty.setRequired(true);
        return booleanProperty;
      case UNION:
        // We can't represent unions, except for the special case of a union with null, which can
        // can be treated as a non-required field.
        // In json-schema, 'required' is really about whether a field is guaranteed to have some
        // value rather than whether that value is permitted to be null. It is unclear whether this
        // is the intended meaning in swagger or not. But the behavior of this code should be
        // acceptable on either interpretation.

        java.util.List<Schema> memberSchemas = Lists.newArrayList();
        for (Schema memberSchema : schema.getTypes()) {
          if (memberSchema.getType() != Schema.Type.NULL) {
            memberSchemas.add(memberSchema);
          }
        }
        if (memberSchemas.size() > 1) {
          LOGGER.debug(
                  "Cannot include schema with union (containing multiple non-null types) in swagger "
                          + "docs: {}", schema);
          return null;
        } else if (memberSchemas.size() < 1) {
          LOGGER.warn("Union has no non-null types, this should be impossible: {}", schema);
          return null;
        }

        Property memberProperty = parseSchema(memberSchemas.get(0));
        if (memberProperty == null) {
          return null;
        }

        Property p = PropertyBuilder.build(memberProperty.getType(), memberProperty.getFormat(), Collections.emptyMap());
        p.setRequired(false);
        p.setDescription(memberProperty.getDescription());

        return p;
      case STRING:
        Property stringProperty = new StringProperty();
        stringProperty.setRequired(true);
        return stringProperty;
      case BYTES:
        Property bytesProperty = new StringProperty("byte");
        bytesProperty.setRequired(true);
        return bytesProperty;
      case INT:
        Property intProperty = new IntegerProperty();
        intProperty.setRequired(true);
        return intProperty;
      case LONG:
        Property longProperty = new LongProperty();
        longProperty.setRequired(true);
        return longProperty;
      case FLOAT:
        Property floatProperty = new FloatProperty();
        floatProperty.setRequired(true);
        return floatProperty;
      case DOUBLE:
        Property doubleProperty = new DoubleProperty();
        doubleProperty.setRequired(true);
        return doubleProperty;
      default:
        // TODO support Schema.Type.FIXED
        // Schema.Type.MAP is ignored because Swagger does not support maps
        return null;
    }
  }

  /**
   * Generate a ModelProperty for the given field.
   *
   * @return the parsed property, or null if the property should be excluded from the Swagger model
   */
  @Nullable
  protected Property parseField(Schema.Field field) {
    Property property = parseSchema(field.schema());
    if (property == null) {
      return null;
    }

    property.setDescription(adjustDescription(field.doc()));

    return property;
  }

  /**
   * Return the Avro schema for the given class, or null if this converter should not handle this
   * class.
   */
  @Nullable
  protected Schema getSchema(Class<?> cls) {
    if (!SpecificRecordBase.class.isAssignableFrom(cls)) {
      return null;
    }

    Field field;
    try {
      field = cls.getDeclaredField("SCHEMA$");
    } catch (NoSuchFieldException e) {
      return null;
    }

    Object schema;
    try {
      schema = field.get(null);
    } catch (IllegalAccessException e) {
      return null;
    }

    if (schema instanceof Schema) {
      return (Schema) schema;
    }

    return null;
  }

  /**
   * Given an Avro field, return the name that should be used for the Swagger property.
   */
  protected String getFieldName(Schema.Field field) {
    return ((PropertyNamingStrategy.SnakeCaseStrategy)
            PropertyNamingStrategy.SNAKE_CASE)
            .translate(field.name());
  }

  /**
   * Given the doc string for an Avro entity, return the Swagger description to use.
   *
   * @param doc the avro doc string to adjust (may be null)
   * @return the adjusted documentation, or null
   */
  @Nullable
  protected String adjustDescription(@Nullable String doc) {
    if (doc == null) {
      return null;
    }
    return doc.replaceAll("(?m)^\\s*\\*\\s?", "");
  }
}
