package com.cerner.beadledom.pagination.parameters;

import com.cerner.beadledom.pagination.OffsetPaginationModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Represent the limit parameter used for pagination.
 *
 * <p>The validation rules needed for a limit parameter are contained here
 * so that the same rules may be applied consistently across all paged
 * resources.
 *
 * @author Brian van de Boogaard
 * @author Will Pruyn
 * @author Ian Kottman
 * @since 3.1
 */
@ApiModel
public class LimitParameter extends AbstractParameter<Integer> {

  @ApiModelProperty(value = "Total number of items to return in the response.", dataType = "int")
  private final String limit;

  @Inject
  @Named("defaultLimit")
  private static Integer defaultLimit;

  @Inject
  @Named("limitFieldName")
  private static String defaultLimitFieldName;

  @Inject
  @Named("maxLimit")
  private static int maxLimit;

  /**
   * Creates an instance of {@link LimitParameter}.
   *
   * @param param the limit value from a request
   */
  public LimitParameter(String param) {
    super(param, defaultLimitFieldName);
    this.limit = param;
  }

  /**
   * Creates an instance of {@link LimitParameter} with a non-default field name.
   *
   * @param param the limit value from a request
   * @param paramFieldName the name of the limit field being
   */
  public LimitParameter(String param, String paramFieldName) {
    super(param, paramFieldName);
    this.limit = param;
  }

  @Override
  protected Integer parse(String param) throws InvalidParameterException {
    Integer limit;
    try {
      limit = Integer.parseInt(this.limit);
    } catch (NumberFormatException e) {
      throw InvalidParameterException.create(
          "Invalid type for '" + this.getParameterFieldName() + "': " + this.limit
              + " - int is required.");
    }

    if (limit < 0 || limit > maxLimit) {
      throw InvalidParameterException.create(
          "Invalid value for '" + this.getParameterFieldName() + "': " + this.limit
              + "  - value between 0 and 100 is required.");
    }

    return limit;
  }

  /**
   * Retrieves the configured default limit value; null if not using the
   * {@link OffsetPaginationModule}.
   * @return the default limit value.
   */
  public static Integer getDefaultLimit() {
    return defaultLimit;
  }

  /**
   * Retrieves the configured default limit field name; null if not using the
   * {@link OffsetPaginationModule}.
   * @return the default limit field name.
   */
  public static String getDefaultLimitFieldName() {
    return defaultLimitFieldName;
  }
}
