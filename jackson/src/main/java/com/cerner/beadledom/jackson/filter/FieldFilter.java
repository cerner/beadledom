package com.cerner.beadledom.jackson.filter;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Jackson filter that will weed out fields from a provided String.
 *
 * <p>The filter will keep only the fields specified. Fields are comma delimited and nested fields
 * can be referenced by their hierarchical field address. This is exemplified by the following:
 *
 * <pre>
 *     id,name,inner_model/name,inner_model2/embedded_inner/id,list_model/id
 * </pre>
 *
 * <p>This will result in producing the following json:
 *
 * <pre>
 *     {
 *         "id": "id1",
 *         "name": "name1",
 *         "inner_model" : {
 *             "name" : "inner_name1"
 *         },
 *         "inner_model2" : {
 *             "embedded_inner" : {
 *                 "id" : "embedded_id1"
 *             }
 *         },
 *         "list_model" : [
 *              {
 *                  "id" : "elem1"
 *              },
 *              {
 *                  "id" : "elem2"
 *              }
 *         ]
 *     }
 * </pre>
 *
 * <p>Using the above example, if you want filtering to be provided for embedded_inner, you'd need
 * to provide the string:
 * <pre>
 *     inner_model2/embedded_inner
 * </pre>
 *
 * <p>This would serialize the entire object for key "inner_model2"/"embedded_inner".
 *
 * <p>Note: json fields containing a '/' character are NOT supported by this filter at this time.
 */
public class FieldFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(FieldFilter.class);
  public static final FieldFilter UNFILTERED_FIELD = new FieldFilter(false);

  private final boolean isFiltered;
  private final Map<String, FieldFilter> filters = Maps.newHashMap();

  private FieldFilter(boolean isFiltered) {
    this.isFiltered = isFiltered;
  }

  private FieldFilter(Set<String> fields) {
    this.isFiltered = true;
    for (String field : fields) {
      field = field.trim();
      addField(field);
    }
  }

  /**
   * Creates a new {@code FieldFilter} to handle empty or null params.
   * @param fieldParam comma separated list of fields to keep when executing the filter; should be
   *     in the format"id,inner_model/id,other_thing" corresponding to field names in the JSON
   * @return a new FieldFilter to be applied to JSON
   */
  public static FieldFilter create(String fieldParam) {
    if (fieldParam == null || fieldParam.isEmpty()) {
      return new FieldFilter(Sets.<String>newHashSet());
    }
    return new FieldFilter(Sets.newHashSet(fieldParam.split(",")));
  }

  /**
   * Adds a field to be filtered for this FieldFilter.
   */
  protected void addField(String field) {
    if (field.contains("/")) {
      // Splits the field into, at most, 2 strings - "prefix" / "suffix" - since we guarantee the
      // field contains a / this will ALWAYS have a length of 2.
      String[] fields = field.split("/", 2);
      String prefix = fields[0];
      // This may be an empty string if the field passed in was "foo/"
      String suffix = fields[1];
      if ("".equals(suffix)) {
        filters.put(prefix, UNFILTERED_FIELD);
        return;
      }

      FieldFilter nestedFilter = filters.get(prefix);
      if (filters.containsKey(prefix) && nestedFilter == UNFILTERED_FIELD) {
        return;
      } else if (nestedFilter == null) {
        nestedFilter = new FieldFilter(true);
        filters.put(prefix, nestedFilter);
      }
      nestedFilter.addField(suffix);
    } else {
      filters.put(field, UNFILTERED_FIELD);
    }
  }

  /**
   * Returns true if this FieldFilter contains filters, false otherwise.
   */
  public boolean hasFilters() {
    return !filters.isEmpty();
  }

  protected Map<String, FieldFilter> getFilters() {
    return filters;
  }

  /**
   * Writes the json from the parser onto the generator, using the filters to only write the objects
   * specified.
   *
   * @param parser JsonParser that is created from a Jackson utility (i.e. ObjectMapper)
   * @param jgen JsonGenerator that is used for writing json onto an underlying stream
   * @throws JsonGenerationException exception if Jackson throws an error while iterating through
   *     the JsonParser
   * @throws IOException if en error occurs while Jackson is parsing or writing json
   */
  public void writeJson(JsonParser parser, JsonGenerator jgen) throws IOException {
    checkNotNull(parser, "JsonParser cannot be null for writeJson.");
    checkNotNull(jgen, "JsonGenerator cannot be null for writeJson.");
    JsonToken curToken = parser.nextToken();
    while (curToken != null) {
      curToken = processToken(curToken, parser, jgen);
    }
    jgen.flush();
  }

  private JsonToken processToken(JsonToken curToken, JsonParser parser, JsonGenerator jgen)
      throws IOException {
    if (curToken.isStructEnd()) {
      // this is our escape and base case.
      return curToken;
    }
    if (curToken.isStructStart()) {
      processStructStart(curToken, jgen);
      JsonToken token = processToken(parser.nextToken(), parser, jgen);
      while (!token.isStructEnd()) {
        token = processToken(token, parser, jgen);
      }
      processStructEnd(token, jgen);
      return parser.nextToken();
    }

    if (curToken.id() == JsonTokenId.ID_FIELD_NAME) {
      String currentName = parser.getCurrentName();
      if (isFiltered && filters.containsKey(parser.getCurrentName())) {
        jgen.writeFieldName(currentName);
        // perform filtering.
        return filters.get(parser.getCurrentName()).processToken(parser.nextToken(), parser, jgen);
      } else if (!isFiltered) {
        jgen.writeFieldName(currentName);
        return processToken(parser.nextToken(), parser, jgen);
      } else {
        parser.nextToken();
        parser.skipChildren();
        return processToken(parser.nextToken(), parser, jgen);
      }
    } else if (curToken.isScalarValue()) {
      processValue(curToken, parser, jgen);
      return parser.nextToken();
    } else {
      LOGGER.error(
          "Unable to process the token {} with name {}.", curToken, parser.getCurrentName());
      throw new RuntimeException(
          "Unable to process the token " + curToken + " with name" + parser.getCurrentName());
    }
  }

  /**
   * Uses a JsonToken + JsonParser to determine how to write a value to the JsonGenerator.
   *
   * <p>This separation exists so we can separate the iteration logic from the parsing logic.
   *
   * @param valueToken current token we are interested in from the parser
   * @param parser current parser
   * @param jgen JsonGenerator that is used for writing json onto an underlying stream
   * @throws JsonGenerationException if access to the JsonParser throws a JsonGenerationException
   * @throws IOException if the Jackson utilties (JsonParser or JsonGenerator) throw an IOException
   */
  private void processValue(JsonToken valueToken, JsonParser parser, JsonGenerator jgen)
      throws IOException {
    if (valueToken.isBoolean()) {
      jgen.writeBoolean(parser.getBooleanValue());
    } else if (valueToken.isNumeric()) {
      if (parser.getNumberType() == JsonParser.NumberType.INT) {
        jgen.writeNumber(parser.getIntValue());
      } else if (parser.getNumberType() == JsonParser.NumberType.DOUBLE) {
        jgen.writeNumber(parser.getDoubleValue());
      } else if (parser.getNumberType() == JsonParser.NumberType.FLOAT) {
        jgen.writeNumber(parser.getFloatValue());
      } else if (parser.getNumberType() == JsonParser.NumberType.LONG) {
        jgen.writeNumber(parser.getLongValue());
      } else if (parser.getNumberType() == JsonParser.NumberType.BIG_DECIMAL) {
        jgen.writeNumber(parser.getDecimalValue());
      } else if (parser.getNumberType() == JsonParser.NumberType.BIG_INTEGER) {
        jgen.writeNumber(parser.getBigIntegerValue());
      } else {
        LOGGER.error("Found unsupported numeric value with name {}.", parser.getCurrentName());
        throw new RuntimeException(
            "Found unsupported numeric value with name " + parser.getCurrentName());
      }
    } else if (valueToken.id() == JsonTokenId.ID_STRING) {
      jgen.writeString(parser.getText());
    } else {
      // Something bad just happened. Probably an unsupported type.
      LOGGER.error(
          "Found unsupported value type {} for name {}.", valueToken.id(), parser.getCurrentName());
      throw new RuntimeException(
          "Found unsupported value type " + valueToken.id() + " for name " + parser
              .getCurrentName());
    }
  }

  private void processStructStart(JsonToken token, JsonGenerator jgen) throws IOException {
    if (token.id() == JsonToken.START_OBJECT.id()) {
      jgen.writeStartObject();
    } else if (token.id() == JsonToken.START_ARRAY.id()) {
      jgen.writeStartArray();
    } else {
      LOGGER.error("Illegal struct start {}", token.id());
      throw new RuntimeException("Illegal struct start " + token.id());
    }
  }

  private void processStructEnd(JsonToken token, JsonGenerator jgen)
      throws IOException {
    if (token.id() == JsonTokenId.ID_END_OBJECT) {
      jgen.writeEndObject();
    } else if (token.id() == JsonTokenId.ID_END_ARRAY) {
      jgen.writeEndArray();
    } else {
      LOGGER.error("Illegal struct end {}", token.id());
      throw new RuntimeException("Illegal struct end " + token.id());
    }
  }
}
