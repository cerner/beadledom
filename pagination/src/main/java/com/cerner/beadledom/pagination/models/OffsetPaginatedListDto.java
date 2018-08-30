package com.cerner.beadledom.pagination.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Offset based pagination list.
 *
 * <p>Represents an offset based paginated list in a rest response.
 *
 * @author John Leacox
 * @since 2.7
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoValue
@JsonPropertyOrder({
    "items",
    "totalResults",
    "firstLink",
    "lastLink",
    "nextLink",
    "prevLink"
})
@JsonDeserialize(builder = OffsetPaginatedListDto.Builder.class)
public abstract class OffsetPaginatedListDto<T> {
  @JsonProperty("items")
  @ApiModelProperty(value = "Array of items for the current page")
  public abstract List<T> items();

  @Nullable
  @JsonProperty("totalResults")
  @ApiModelProperty(value = "Total count of items across all pages")
  public abstract Long totalResults();

  @JsonProperty("firstLink")
  public abstract String firstLink();

  @JsonProperty("lastLink")
  @Nullable
  public abstract String lastLink();

  @JsonProperty("prevLink")
  @Nullable
  public abstract String prevLink();

  @JsonProperty("nextLink")
  @Nullable
  public abstract String nextLink();

  /**
   * Creates a builder for {@link OffsetPaginatedListDto}.
   *
   * @return instance of {@link Builder}
   */
  public static <T> Builder<T> builder() {
    return new AutoValue_OffsetPaginatedListDto.Builder<T>()
        .items(new ArrayList<>());
  }

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder<T> {
    @JsonCreator
    private static OffsetPaginatedListDto.Builder create() {
      return OffsetPaginatedListDto.builder();
    }

    @JsonProperty("items")
    public abstract Builder<T> items(List<T> items);

    @JsonProperty("totalResults")
    public abstract Builder<T> totalResults(Long totalResults);

    @JsonProperty("firstLink")
    public abstract Builder<T> firstLink(String firstLink);

    @JsonProperty("lastLink")
    public abstract Builder<T> lastLink(String lastLink);

    @JsonProperty("nextLink")
    public abstract Builder<T> nextLink(String nextLink);

    @JsonProperty("prevLink")
    public abstract Builder<T> prevLink(String prevLink);

    public abstract OffsetPaginatedListDto<T> build();
  }
}
