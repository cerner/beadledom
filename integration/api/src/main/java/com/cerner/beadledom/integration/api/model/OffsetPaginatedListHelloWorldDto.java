package com.cerner.beadledom.integration.api.model;

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
 * Client implementation of an offset based pagination list of {@link HelloWorldDto}.
 *
 * <p>Represents an offset based paginated list in a rest response.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoValue
@JsonDeserialize(builder = OffsetPaginatedListHelloWorldDto.Builder.class)
public abstract class OffsetPaginatedListHelloWorldDto implements OffsetPaginatedListDto<HelloWorldDto> {

  @JsonProperty("items")
  public abstract List<HelloWorldDto> items();

  /**
   * Creates a builder for {@link OffsetPaginatedListHelloWorldDto}.
   *
   * @return instance of {@link Builder}
   */
  public static Builder builder() {
    return new AutoValue_OffsetPaginatedListHelloWorldDto.Builder().items(new ArrayList<>());
  }

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder implements OffsetPaginatedListDto.Builder<Builder, HelloWorldDto> {

    @JsonCreator
    public static OffsetPaginatedListHelloWorldDto.Builder create() {
      return OffsetPaginatedListHelloWorldDto.builder();
    }

    @JsonProperty("items")
    public abstract Builder items(List<HelloWorldDto> items);

    public abstract OffsetPaginatedListHelloWorldDto build();
  }
}
