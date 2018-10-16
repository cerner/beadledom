package com.cerner.beadledom.pagination.models;

import com.google.auto.value.AutoValue;

/**
 * Configuration for offset based pagination. Allows configuration of the parameter names, their
 * default values, and their allowed values.
 *
 * @author Ian Kottman
 * @since 3.1
 */
@AutoValue
public abstract class OffsetPaginationConfiguration {

  /**
   * Maximum allowed value for limit. Defaults to 100.
   */
  public abstract Integer maxLimit();

  /**
   * Default value for limit. Defaults to 20.
   */
  public abstract Integer defaultLimit();

  /**
   * Query parameter name for limit. Defaults to limit.
   */
  public abstract String limitFieldName();

  /**
   * Default value for offset. Defaults to 0.
   */
  public abstract Long defaultOffset();

  /**
   * Query parameter name for offset. Defaults to offset.
   */
  public abstract String offsetFieldName();

  /**
   * Flag to denote whether limit value of 0 is allowed. Defaults to false.
   */
  public abstract boolean allowZeroLimit();

  /**
   * Creates a builder for {@link OffsetPaginationConfiguration}.
   *
   * @return instance of {@link Builder}
   */
  public static Builder builder() {
    return new AutoValue_OffsetPaginationConfiguration.Builder()
        .setDefaultOffset(0L)
        .setDefaultLimit(20)
        .setMaxLimit(100)
        .setLimitFieldName("limit")
        .setOffsetFieldName("offset")
        .setAllowZeroLimit(false);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setMaxLimit(Integer value);

    public abstract Builder setDefaultOffset(Long value);

    public abstract Builder setDefaultLimit(Integer value);

    public abstract Builder setLimitFieldName(String value);

    public abstract Builder setOffsetFieldName(String value);

    public abstract Builder setAllowZeroLimit(boolean allowZeroLimit);

    public abstract OffsetPaginationConfiguration build();
  }
}
