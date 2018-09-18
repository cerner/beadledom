package com.cerner.beadledom.pagination;

import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import java.util.ArrayList;

/**
 * Offset based pagination list.
 *
 * <p>Represents an offset based paginated list in a rest response.
 *
 * @author John Leacox
 * @since 3.2
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
@JsonDeserialize(builder = OffsetPaginatedListDtoImpl.Builder.class)
abstract class OffsetPaginatedListDtoImpl<T> implements OffsetPaginatedListDto<T> {

  /**
   * Creates a builder for {@link OffsetPaginatedListDtoImpl}.
   *
   * @return instance of {@link Builder}
   */
  public static <T> Builder<T> builder() {
    return new AutoValue_OffsetPaginatedListDtoImpl.Builder<T>()
        .items(new ArrayList<>());
  }

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder<T> implements OffsetPaginatedListDto.Builder<Builder<T>, T> {
    @JsonCreator
    private static OffsetPaginatedListDtoImpl.Builder create() {
      return OffsetPaginatedListDtoImpl.builder();
    }

    public abstract OffsetPaginatedListDtoImpl<T> build();

  }
}
