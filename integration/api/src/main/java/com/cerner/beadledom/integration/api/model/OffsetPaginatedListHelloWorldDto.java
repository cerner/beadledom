package com.cerner.beadledom.integration.api.model;

import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import java.util.ArrayList;

/**
 * Client implementation of an offset based pagination list of {@link HelloWorldDto}.
 *
 * @author Nick Behrens
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoValue
@JsonDeserialize(builder = OffsetPaginatedListHelloWorldDto.Builder.class)
public abstract class OffsetPaginatedListHelloWorldDto implements OffsetPaginatedListDto<HelloWorldDto> {

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

    public abstract OffsetPaginatedListHelloWorldDto build();
  }
}
