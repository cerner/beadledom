package com.cerner.beadledom.pagination.parameters;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * Query parameters for an endpoint with offset based pagination.
 *
 * <p>This class can be added with the @BeanParam annotation to add the query parameters needed
 * to support offset based pagination. Allows for injection of default values for limit and offset.
 * </p>
 *
 * <p>*NOTE* This will not work with configured field names.</p>
 *
 * @author Will Pruyn
 * @since 3.1
 */
public class OffsetPaginationParameters {
  @Context
  UriInfo uriInfo;

  /**
   * Retrieves the value of the limit field from the request. Will use the configured field name
   * to find the parameter.
   *
   * @return The value of the limit.
   */
  public Integer getLimit() {
    String limitFromRequest =
        uriInfo.getQueryParameters().getFirst(LimitParameter.getDefaultLimitFieldName());

    return limitFromRequest != null ? new LimitParameter(limitFromRequest).getValue()
        : LimitParameter.getDefaultLimit();
  }

  /**
   * Retrieves the value of the offset field from the request. Will use the configured field name
   * to find the parameter
   *
   * @return The value of the offset.
   */
  public Long getOffset() {
    String offsetFromRequest =
        uriInfo.getQueryParameters().getFirst(OffsetParameter.getDefaultOffsetFieldName());

    return offsetFromRequest != null ? new OffsetParameter(offsetFromRequest).getValue()
        : OffsetParameter.getDefaultOffset();
  }
}
