package com.cerner.beadledom.pagination.models;

import com.cerner.beadledom.pagination.OffsetPaginatedList;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Offset based pagination list.
 *
 * <p>Represents an offset based paginated list in a rest response. Is used on the client side
 * instead of {@link OffsetPaginatedList} to circumvent type erasure.
 */
public interface OffsetPaginatedListDto<T> {

  @JsonProperty("totalResults")
  @Nullable
  Long totalResults();

  @JsonProperty("firstLink")
  String firstLink();

  @JsonProperty("lastLink")
  @Nullable
  String lastLink();

  @JsonProperty("prevLink")
  @Nullable
  String prevLink();

  @JsonProperty("nextLink")
  @Nullable
  String nextLink();

  @JsonProperty("items")
  List<T> items();

  interface Builder<B extends Builder<B, T>, T> {

    @JsonProperty("items")
    B items(List<T> items);

    @JsonProperty("totalResults")
    B totalResults(Long totalResults);

    @JsonProperty("firstLink")
    B firstLink(String firstLink);

    @JsonProperty("lastLink")
    B lastLink(String lastLink);

    @JsonProperty("nextLink")
    B nextLink(String nextLink);

    @JsonProperty("prevLink")
    B prevLink(String prevLink);

    OffsetPaginatedListDto<T> build();
  }
}
