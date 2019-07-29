package com.cerner.beadledom.pagination;

import com.cerner.beadledom.pagination.parameters.LimitParameter;
import com.cerner.beadledom.pagination.parameters.OffsetParameter;
import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Offset based pagination list.
 *
 * @author Will Pruyn
 * @since 3.1
 */
@AutoValue
public abstract class OffsetPaginatedList<T> {
  /**
   * Returns a list of items for the current page.
   */
  public abstract List<T> items();

  /**
   * Returns the {@link OffsetPaginatedListMetadata} for the {@link OffsetPaginatedList};
   * {@link OffsetPaginationModule}.
   */
  public abstract OffsetPaginatedListMetadata metadata();

  /**
   * Creates a builder for {@link OffsetPaginatedList}.
   *
   * @return instance of {@link OffsetPaginatedList.Builder}
   */
  public static <T> OffsetPaginatedList.Builder<T> builder() {
    return new AutoValue_OffsetPaginatedList.Builder<T>()
        .items(new ArrayList<>())
        .metadata(OffsetPaginatedListMetadata.builder().build());
  }

  @AutoValue.Builder
  public abstract static class Builder<T> {

    public abstract OffsetPaginatedList.Builder<T> items(List<T> items);

    public abstract OffsetPaginatedList.Builder<T> metadata(OffsetPaginatedListMetadata metadata);

    /**
     * Convenience method that allows metadata to be set without having to manually build a
     * {@link OffsetPaginatedListMetadata} object.
     *
     * @param limitFieldName the limit field name used to create the page
     * @param limit the limit used to create the page
     * @param offsetFieldName the offset field name used to create the page
     * @param offset the offset used to create the page
     * @param totalResults the total results available
     * @param hasMore indicates if there are more results available
     * @return The {@link Builder} being used for the {@link OffsetPaginatedList}
     * @deprecated use {@link #metadata(OffsetPaginatedListMetadata)}, possibly with a builder
     *     instead
     */
    @Deprecated
    public OffsetPaginatedList.Builder<T> metadata(
        @Nullable String limitFieldName, @Nullable Integer limit, @Nullable String offsetFieldName,
        @Nullable Long offset, @Nullable Long totalResults, @Nullable Boolean hasMore) {
      return metadata(OffsetPaginatedListMetadata.builder()
          .limitFieldName(limitFieldName)
          .limit(limit)
          .offsetFieldName(offsetFieldName)
          .offset(offset)
          .totalResults(totalResults)
          .hasMore(hasMore)
          .build()
      );
    }

    /**
     * Convenience method that allows metadata to be set using configured field names
     * without having to manually build a {@link OffsetPaginatedListMetadata} object.
     *
     * @param limit the limit used to create the page
     * @param offset the offset used to create the page
     * @param totalResults the total results available
     * @param hasMore indicates if there are more results available
     * @return The {@link Builder} being used for the {@link OffsetPaginatedList}
     * @deprecated use {@link #metadata(OffsetPaginatedListMetadata)}, possibly with a builder
     *     instead
     */
    @Deprecated
    public OffsetPaginatedList.Builder<T> metadata(
        @Nullable Integer limit, @Nullable Long offset, @Nullable Long totalResults,
        @Nullable Boolean hasMore) {
      return metadata(
          LimitParameter.getDefaultLimitFieldName(), limit,
          OffsetParameter.getDefaultOffsetFieldName(), offset, totalResults, hasMore);
    }

    /**
     * Convenience method that creates metadata with total results and default field names
     * without having to manually build a {@link OffsetPaginatedListMetadata} object.
     *
     * @param totalResults the total results available
     * @return the {@link Builder} being used for the {@link OffsetPaginatedList}
     * @deprecated use {@link #totalResults(Long)} instead
     */
    @Deprecated
    public OffsetPaginatedList.Builder<T> metadata(@Nullable Long totalResults) {
      return totalResults(totalResults);
    }

    /**
     * Convenience method that adds a metadata instance with total results and default field names
     * without having to manually build an {@link OffsetPaginatedListMetadata} object.
     *
     * @param totalResults the total results available
     * @return the {@link Builder} being used for the {@link OffsetPaginatedList}
     */
    public OffsetPaginatedList.Builder<T> totalResults(@Nullable Long totalResults) {
      return metadata(
          OffsetPaginatedListMetadata.builder()
              .totalResults(totalResults)
              .build());
    }

    abstract OffsetPaginatedList<T> autoBuild();

    /**
     * Builds a new instance of {@link OffsetPaginatedList}.
     */
    public OffsetPaginatedList<T> build() {
      OffsetPaginatedList<T> paginatedList = autoBuild();

      Long totalResults = paginatedList.metadata().totalResults();
      if (totalResults != null && totalResults < 0) {
        throw new IllegalStateException("totalResults is negative");
      }

      return paginatedList;
    }
  }
}
