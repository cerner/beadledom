package com.cerner.beadledom.pagination;

import com.cerner.beadledom.pagination.parameters.LimitParameter;
import com.cerner.beadledom.pagination.parameters.OffsetParameter;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given an {@link OffsetPaginatedList} this class encapsulates the pagination links for the list.
 *
 * @author John Leacox
 * @author Will Pruyn
 * @since 3.1
 */
class OffsetPaginationLinks {
  private final Logger logger = LoggerFactory.getLogger(OffsetPaginationLinks.class);

  private final UriInfo uriInfo;
  private final Long totalResults;
  private final Boolean hasMore;
  private final Long currentOffset;
  private final String currentOffsetFieldName;
  private final Integer currentLimit;
  private final String currentLimitFieldName;

  private OffsetPaginationLinks(
      OffsetPaginatedList list, UriInfo uriInfo, Long currentOffset, Integer currentLimit) {
    this.uriInfo = uriInfo;
    this.totalResults = list.metadata().totalResults();
    this.hasMore = list.metadata().hasMore();
    this.currentOffset = currentOffset;
    this.currentOffsetFieldName = list.metadata().offsetFieldName();
    this.currentLimit = currentLimit;
    this.currentLimitFieldName = list.metadata().limitFieldName();
  }

  /**
   * Creates a new instance of {@code OffsetPaginationLinks} for the provided list.
   *
   * @param list the list to provide links for
   * @param uriInfo the {@link UriInfo} for the current request
   */
  public static OffsetPaginationLinks create(
      OffsetPaginatedList<?> list, UriInfo uriInfo) {
    // Find offset and limit by inspecting the list metadata, if that's not provided then fall back
    // to inspecting the UriInfo.
    Long offset =
        list.metadata().offset() != null ? list.metadata().offset()
            : currentOffset(uriInfo, list.metadata().offsetFieldName());
    Integer limit =
        list.metadata().limit() != null ? list.metadata().limit()
            : currentLimit(uriInfo, list.metadata().limitFieldName());

    return new OffsetPaginationLinks(list, uriInfo, offset, limit);
  }

  private static Long currentOffset(UriInfo uriInfo, String offsetFieldName) {
    String offset = uriInfo.getQueryParameters().getFirst(offsetFieldName);
    return offset != null ? new OffsetParameter(offset, offsetFieldName).getValue()
        : OffsetParameter.getDefaultOffset();
  }

  private static Integer currentLimit(UriInfo uriInfo, String limitFieldName) {
    String limit = uriInfo.getQueryParameters().getFirst(limitFieldName);
    return limit != null ? new LimitParameter(limit, limitFieldName).getValue()
        : LimitParameter.getDefaultLimit();
  }

  /**
   * Returns the first page link.
   */
  String firstLink() {
    return urlWithUpdatedPagination(0L, currentLimit);
  }

  /**
   * Returns the last page link; null if no last page link is available.
   */
  String lastLink() {
    if (totalResults == null || currentLimit == 0L) {
      return null;
    }

    Long lastOffset;
    if (totalResults % currentLimit == 0L) {
      lastOffset = totalResults - currentLimit;
    } else {
      // Truncation due to integral division gives floor-like behavior for free.
      lastOffset = totalResults / currentLimit * currentLimit;
    }

    return urlWithUpdatedPagination(lastOffset, currentLimit);
  }

  /**
   * Returns the next page link; null if no next page link is available.
   */
  String nextLink() {
    if (!hasNext()) {
      return null;
    }

    return urlWithUpdatedPagination(currentOffset + currentLimit, currentLimit);
  }

  /**
   * Returns the next prev link; null if no prev page link is available.
   */
  String prevLink() {
    if (currentOffset == 0 || currentLimit == 0) {
      return null;
    }

    return urlWithUpdatedPagination(Math.max(0, currentOffset - currentLimit), currentLimit);
  }

  private boolean hasNext() {
    if (currentLimit == 0) {
      return false;
    }

    boolean moreResults = totalResults != null && (currentOffset + currentLimit < totalResults);

    if (totalResults == null) {
      return hasMore != null && hasMore;
    }

    if (hasMore == null) {
      return moreResults;
    }

    if (hasMore != moreResults) {
      logger.warn(
          "Conflict between hasMore [{}] and totalResults [{}] at url [{}]; "
              + "next page link will be generated anyway.",
          hasMore, totalResults, uriInfo.getRequestUri());
    }

    return hasMore || moreResults;
  }

  private String urlWithUpdatedPagination(Long offset, Integer limit) {
    return uriInfo.getRequestUriBuilder()
        .replaceQueryParam(currentOffsetFieldName, offset)
        .replaceQueryParam(currentLimitFieldName, limit)
        .build().toString();
  }
}
