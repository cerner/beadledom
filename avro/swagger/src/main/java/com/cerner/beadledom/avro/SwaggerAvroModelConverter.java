package com.cerner.beadledom.avro;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.collect.Lists;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.core.SwaggerTypes;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.AnyAllowableValues$;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;
import com.wordnik.swagger.model.ModelRef;
import java.lang.reflect.Field;
import java.util.Collections;
import javax.annotation.Nullable;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.immutable.Map;
import scala.collection.mutable.LinkedHashMap;

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
public class SwaggerAvroModelConverter extends SwaggerSchemaConverter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerAvroModelConverter.class);

  @Override
  public Option<Model> read(Class<?> cls, Map<String, String> typeMap) {
    Schema schema = getSchema(cls);
    if (schema == null) {
      return Option.empty();
    }

    LinkedHashMap<String, ModelProperty> properties = new LinkedHashMap<>();
    for (Schema.Field field : schema.getFields()) {
      ModelProperty property = parseField(field);
      if (property == null) {
        LOGGER.debug(
            "Omitted field {} of schema {} from swagger docs", field.name(), schema.getName());
      } else {
        properties.update(getFieldName(field), property);
      }
    }

    return Option.apply(
        new Model(
            toName(cls),
            toName(cls),
            cls.getName(),
            properties,
            toDescriptionOpt(cls),
            Option.<String>empty(),
            Option.<String>empty(),
            JavaConversions.asScalaBuffer(Collections.<String>emptyList()).toList()));
  }

  @Override
  public String toName(Class<?> cls) {
    Schema schema = getSchema(cls);
    if (schema == null) {
      return super.toName(cls);
    }

    return getName(schema);
  }

  @Override
  public Option<String> toDescriptionOpt(Class<?> cls) {
    Schema schema = getSchema(cls);
    if (schema == null) {
      return Option.empty();
    }

    return Option.apply(adjustDescription(schema.getDoc()));
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
   * @return the parsed property, or null if it cannot/should not be represented in the Swagger
   *     model
   */
  @Nullable
  protected ModelProperty parseSchema(Schema schema) {
    switch (schema.getType()) {
      case RECORD:
        return simpleProperty(getName(schema), schema.getFullName());
      case ENUM:
        // TODO may wish to include the enum's documentation in the field documentation, since it
        // won't appear anywhere else
        return new ModelProperty(
            "string",
            "string",
            0,
            true,
            Option.<String>empty(),
            new AllowableListValues(
                JavaConversions.asScalaBuffer(schema.getEnumSymbols()).toList(),
                "LIST"),
            Option.<ModelRef>empty()
        );
      case ARRAY:
        ModelProperty elementsProperty = parseSchema(schema.getElementType());
        if (elementsProperty == null) {
          return null;
        }

        if (SwaggerSpec.containerTypes().contains(elementsProperty.type())) {
          LOGGER.debug("Cannot include nested collection schema in swagger docs: {}", schema);
          return null;
        }
        return new ModelProperty(
            "List",
            "List",
            elementsProperty.position(),
            true,
            elementsProperty.description(),
            elementsProperty.allowableValues(),
            Option.apply(modelRef(elementsProperty))
        );
      case BOOLEAN:
        return simpleProperty("boolean", "boolean");
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

        ModelProperty memberProperty = parseSchema(memberSchemas.get(0));
        if (memberProperty == null) {
          return null;
        }

        return new ModelProperty(
            memberProperty.type(),
            memberProperty.qualifiedType(),
            memberProperty.position(),
            false,
            memberProperty.description(),
            memberProperty.allowableValues(),
            memberProperty.items()
        );
      case STRING:
        return simpleProperty("string", "string");
      case BYTES:
        return simpleProperty("byte", "byte");
      case INT:
        return simpleProperty("int", "int");
      case LONG:
        return simpleProperty("long", "long");
      case FLOAT:
        return simpleProperty("float", "float");
      case DOUBLE:
        return simpleProperty("double", "double");
      default:
        // TODO support Schema.Type.FIXED
        // Schema.Type.MAP is ignored because Swagger does not support maps
        return null;
    }
  }

  private ModelProperty simpleProperty(String type, String qualifiedType) {
    return new ModelProperty(
        type,
        qualifiedType,
        0,
        true,
        Option.<String>empty(),
        AnyAllowableValues$.MODULE$,
        Option.<ModelRef>empty()
    );
  }

  private ModelRef modelRef(ModelProperty target) {
    if (SwaggerTypes.primitives().contains(target.type())) {
      return new ModelRef(
          target.type(),
          Option.<String>empty(),
          Option.apply(target.qualifiedType())
      );
    }

    return new ModelRef(
        null,
        Option.apply(target.type()),
        Option.apply(target.qualifiedType())
    );
  }

  /**
   * Generate a ModelProperty for the given field.
   *
   * @return the parsed property, or null if the property should be excluded from the Swagger model
   */
  @Nullable
  protected ModelProperty parseField(Schema.Field field) {
    ModelProperty property = parseSchema(field.schema());
    if (property == null) {
      return null;
    }

    return new ModelProperty(
        property.type(),
        property.qualifiedType(),
        field.pos(),
        property.required(),
        Option.apply(adjustDescription(field.doc())),
        property.allowableValues(),
        property.items()
    );
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
