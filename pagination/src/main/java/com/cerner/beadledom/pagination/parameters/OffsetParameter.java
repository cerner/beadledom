package com.cerner.beadledom.pagination.parameters;

import com.cerner.beadledom.pagination.BeadledomPaginationModule;
import com.cerner.beadledom.pagination.parameters.exceptions.InvalidParameterException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;

/**
 * Represent the offset parameter used for pagination.
 *
 * <p>The validation rules needed for a offset parameter are contained here
 * so that the same rules may be applied consistently across all paged
 * resources.
 *
 * @author Brian van de Boogaard
 * @author Will Pruyn
 * @since 2.7
 */
@ApiModel
public class OffsetParameter extends AbstractParameter<Long> {

  @ApiModelProperty(value = "Number of items to offset the response by.", dataType = "int",
      allowableValues = "range[0, " + Long.MAX_VALUE + "]")
  @Pattern(regexp = "^0$|^[1-9][0-9]*$", message = "offset must be greater than or equal to zero")
  private final String offset;

  @Inject
  @Named("defaultOffset")
  private static Long defaultOffset;

  @Inject
  @Named("defaultOffsetFieldName")
  private static String defaultOffsetFieldName;

  /**
   * Creates an instance of {@link OffsetParameter}.
   *
   * @param param the offset value from a request
   */
  public OffsetParameter(String param) {
    super(param, defaultOffsetFieldName);
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
   * {@link BeadledomPaginationModule}.
   * @return the default offset value.
   */
  public static Long getDefaultOffset() {
    return defaultOffset;
  }

  /**
   * Retrieves the configured default offset field name; null if not using the
   * {@link BeadledomPaginationModule}.
   * @return the default offset field name.
   */
  public static String getDefaultOffsetFieldName() {
    return defaultOffsetFieldName;
  }
}
