package com.cerner.beadledom.client.example.model;

import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Client implementation of an offset based pagination list of {@link JsonOne}.
 *
 * <p>Represents an offset based paginated list in a rest response.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoValue
@JsonDeserialize(builder = JsonOneOffsetPaginatedListDto.Builder.class)
public abstract class JsonOneOffsetPaginatedListDto implements OffsetPaginatedListDto<JsonOne> {

  @JsonProperty("items")
  public abstract List<JsonOne> items();

  /**
   * Creates a builder for {@link JsonOneOffsetPaginatedListDto}.
   *
   * @return instance of {@link Builder}
   */
  public static Builder builder() {
    return new AutoValue_JsonOneOffsetPaginatedListDto.Builder().items(new ArrayList<>());
  }

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder implements OffsetPaginatedListDto.Builder<Builder, JsonOne> {

    @JsonCreator
    public static JsonOneOffsetPaginatedListDto.Builder create() {
      return JsonOneOffsetPaginatedListDto.builder();
    }

    @JsonProperty("items")
    public abstract Builder items(List<JsonOne> items);

    public abstract JsonOneOffsetPaginatedListDto build();
  }
}
