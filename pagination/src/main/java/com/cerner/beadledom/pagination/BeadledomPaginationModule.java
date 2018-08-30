package com.cerner.beadledom.pagination;

import com.cerner.beadledom.pagination.parameters.LimitParameter;
import com.cerner.beadledom.pagination.parameters.OffsetParameter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import javax.inject.Singleton;

/**
 * Module to provide the necessary configurations for using Offset Based Pagination.
 *
 * <p>Module provides the configurations needed to use offset based pagination. This includes
 * the {@link OffsetPaginatedListLinksWriterInterceptor} as well as default values for the limit and
 * offset fields and their respective field names. The default values for the limit, offset, and
 * their respective field names can be configured using one of the provided constructors.
 *
 * @author Will Pruyn
 * @since 2.7
 */
public class BeadledomPaginationModule extends AbstractModule {
  private final Integer defaultLimit;
  private final String defaultLimitFieldName;
  private final Long defaultOffset;
  private final String defaultOffsetFieldName;

  private static final Integer BEADLEDOM_DEFAULT_LIMIT = 20;
  private static final String BEADLEDOM_DEFAULT_LIMIT_FIELD_NAME = "limit";
  private static final Long BEADLEDOM_DEFAULT_OFFSET = 0L;
  private static final String BEADLEDOM_DEFAULT_OFFSET_FIELD_NAME = "offset";

  /**
   * Default constructor that sets the configurable fields using the Beadledom defaults.
   */
  public BeadledomPaginationModule() {
    this.defaultLimit = BEADLEDOM_DEFAULT_LIMIT;
    this.defaultOffset = BEADLEDOM_DEFAULT_OFFSET;
    this.defaultLimitFieldName = BEADLEDOM_DEFAULT_LIMIT_FIELD_NAME;
    this.defaultOffsetFieldName = BEADLEDOM_DEFAULT_OFFSET_FIELD_NAME;
  }

  /**
   * Constructor that enables setting of default values for the offset and limit fields.
   * Default field names will be set using Beadledom defaults.
   * @param defaultLimit the default value for the limit field; 20 if null.
   * @param defaultOffset the default value for the offset field; 0 if null.
   */
  public BeadledomPaginationModule(Integer defaultLimit, Long defaultOffset) {
    this.defaultLimit = defaultLimit != null ? defaultLimit : BEADLEDOM_DEFAULT_LIMIT;
    this.defaultOffset = defaultOffset != null ? defaultOffset : BEADLEDOM_DEFAULT_OFFSET;
    this.defaultLimitFieldName = BEADLEDOM_DEFAULT_LIMIT_FIELD_NAME;
    this.defaultOffsetFieldName = BEADLEDOM_DEFAULT_OFFSET_FIELD_NAME;
  }

  /**
   * Constructor that enables setting of default values for the offset and limit field names.
   * Default values for these fields will be set using Beadledom defaults.
   * @param defaultLimitFieldName the default value for the limit field name; limit if null.
   * @param defaultOffsetFieldName the default value for the offset field name; offset if null.
   */
  public BeadledomPaginationModule(
      String defaultLimitFieldName, String defaultOffsetFieldName) {
    this.defaultLimit = BEADLEDOM_DEFAULT_LIMIT;
    this.defaultOffset = BEADLEDOM_DEFAULT_OFFSET;
    this.defaultLimitFieldName =
        defaultLimitFieldName != null ? defaultLimitFieldName : BEADLEDOM_DEFAULT_LIMIT_FIELD_NAME;
    this.defaultOffsetFieldName = defaultOffsetFieldName != null ? defaultOffsetFieldName
        : BEADLEDOM_DEFAULT_OFFSET_FIELD_NAME;
  }

  /**
   * Constructor that enables setting of default values for the offset, limit, and their
   * respective field names.
   * @param defaultLimitFieldName the default value for the limit field name; limit if null.
   * @param defaultLimit the default value for the limit field; 20 if null.
   * @param defaultOffsetFieldName the default value for the offset field name; offset if null.
   * @param defaultOffset the default value for the offset field; 0 if null.
   */
  public BeadledomPaginationModule(
      String defaultLimitFieldName, Integer defaultLimit, String defaultOffsetFieldName,
      Long defaultOffset) {
    this.defaultLimit = defaultLimit != null ? defaultLimit : BEADLEDOM_DEFAULT_LIMIT;
    this.defaultOffset = defaultOffset != null ? defaultOffset : BEADLEDOM_DEFAULT_OFFSET;
    this.defaultLimitFieldName =
        defaultLimitFieldName != null ? defaultLimitFieldName : BEADLEDOM_DEFAULT_LIMIT_FIELD_NAME;
    this.defaultOffsetFieldName = defaultOffsetFieldName != null ? defaultOffsetFieldName
        : BEADLEDOM_DEFAULT_OFFSET_FIELD_NAME;
  }

  @Override
  protected void configure() {
    requestStaticInjection(LimitParameter.class);
    requestStaticInjection(OffsetParameter.class);

    bind(OffsetPaginatedListLinksWriterInterceptor.class);
  }

  @Provides
  @Singleton
  @Named("defaultLimit")
  Integer provideDefaultLimit() {
    return defaultLimit;
  }

  @Provides
  @Singleton
  @Named("defaultOffset")
  Long provideDefaultOffset() {
    return defaultOffset;
  }

  @Provides
  @Singleton
  @Named("defaultLimitFieldName")
  String provideDefaultLimitFieldName() {
    return defaultLimitFieldName;
  }

  @Provides
  @Singleton
  @Named("defaultOffsetFieldName")
  String provideDefaultOffsetFieldName() {
    return defaultOffsetFieldName;
  }
}
