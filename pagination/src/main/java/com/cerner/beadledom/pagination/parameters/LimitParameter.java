package com.cerner.beadledom.pagination.parameters;

import com.cerner.beadledom.pagination.OffsetPaginationModule;
import com.cerner.beadledom.pagination.models.OffsetPaginationConfiguration;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.ApiParam;

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
public class LimitParameter extends AbstractParameter<Integer> {

  @ApiParam(value = "Total number of items to return in the response.")
  @io.swagger.annotations.ApiParam(value = "Total number of items to return in the response.")
  private final String limit;

  @Inject
  private static OffsetPaginationConfiguration offsetPaginationConfiguration;

  /**
   * Creates an instance of {@link LimitParameter}.
   *
   * @param param the limit value from a request
   */
  public LimitParameter(String param) {
    super(param, offsetPaginationConfiguration.limitFieldName());
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

    checkLimitRange(limit);

    return limit;
  }

  /**
   * Ensures that the limit is in the allowed range.
   *
   * @param limit the parsed limit value to check the range of
   * @throws InvalidParameterException if the limit value is less outside of the allowed range
   */
  private void checkLimitRange(int limit) {
    int minLimit = offsetPaginationConfiguration.allowZeroLimit() ? 0 : 1;

    if (limit < minLimit || limit > offsetPaginationConfiguration.maxLimit()) {
      throw InvalidParameterException.create(
          "Invalid value for '" + this.getParameterFieldName() + "': " + limit
              + "  - value between " + minLimit + " and " + offsetPaginationConfiguration.maxLimit()
              + " is required.");
    }
  }

  /**
   * Retrieves the configured default limit value; null if not using the
   * {@link OffsetPaginationModule}.
   * @return the default limit value.
   */
  public static Integer getDefaultLimit() {
    return offsetPaginationConfiguration.defaultLimit();
  }

  /**
   * Retrieves the configured default limit field name; null if not using the
   * {@link OffsetPaginationModule}.
   * @return the default limit field name.
   */
  public static String getDefaultLimitFieldName() {
    return offsetPaginationConfiguration.limitFieldName();
  }
}
