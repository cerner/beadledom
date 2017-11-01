package com.cerner.beadledom.pagination.parameters;

import com.cerner.beadledom.pagination.OffsetPaginationModule;
import com.cerner.beadledom.pagination.models.OffsetPaginationConfiguration;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Represent the offset parameter used for pagination.
 *
 * <p>The validation rules needed for a offset parameter are contained here
 * so that the same rules may be applied consistently across all paged
 * resources.
 *
 * @author Brian van de Boogaard
 * @author Will Pruyn
 * @since 3.1
 */
public class OffsetParameter extends AbstractParameter<Long> {

  @ApiParam(value = "Number of items to offset the response by.",
      allowableValues = "range[0, " + Long.MAX_VALUE + "]")
  @io.swagger.annotations.ApiParam(value = "Number of items to offset the response by.",
      allowableValues = "range[0, " + Long.MAX_VALUE + "]")
  private final String offset;

  @Inject
  private static OffsetPaginationConfiguration offsetPaginationConfiguration;

  /**
   * Creates an instance of {@link OffsetParameter}.
   *
   * @param param the offset value from a request
   */
  public OffsetParameter(String param) {
    super(param, offsetPaginationConfiguration.offsetFieldName());
    this.offset = param;
  }

  /**
   * Creates an instance of {@link OffsetParameter} with a non-default field name.
   *
   * @param param the offset value from a request
   */
  public OffsetParameter(String param, String paramFieldName) {
    super(param, paramFieldName);
    this.offset = param;
  }

  @Override
  protected Long parse(String param) throws InvalidParameterException {
    Long offset;
    try {
      offset = Long.parseLong(this.offset);
    } catch (NumberFormatException e) {
      throw InvalidParameterException.create(
          "Invalid type for '" + this.getParameterFieldName() + "': " + this.offset
              + " - int is required.");
    }

    if (offset < 0) {
      throw InvalidParameterException.create(
          "Invalid value for '" + this.getParameterFieldName() + "': " + this.offset
              + " - positive value or zero required.");
    }

    return offset;
  }

  /**
   * Retrieves the configured default offset value; null if not using the
   * {@link OffsetPaginationModule}.
   * @return the default offset value.
   */
  public static Long getDefaultOffset() {
    return offsetPaginationConfiguration.defaultOffset();
  }

  /**
   * Retrieves the configured default offset field name; null if not using the
   * {@link OffsetPaginationModule}.
   * @return the default offset field name.
   */
  public static String getDefaultOffsetFieldName() {
    return offsetPaginationConfiguration.offsetFieldName();
  }
}
