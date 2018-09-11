package com.cerner.beadledom.pagination;

import com.cerner.beadledom.pagination.models.OffsetPaginationConfiguration;
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
 * @author Ian Kottman
 * @since 3.1
 */
public class OffsetPaginationModule extends AbstractModule {
  private final OffsetPaginationConfiguration config;

  /**
   * Default constructor that sets the configurable fields using the Beadledom defaults. For
   * the default values see {@link OffsetPaginationConfiguration}
   */
  public OffsetPaginationModule() {
    this.config = OffsetPaginationConfiguration.builder().build();
  }

  /**
   * Override Beadledom defaults.
   */
  public OffsetPaginationModule(OffsetPaginationConfiguration config) {
    this.config = config;
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
    return config.defaultLimit();
  }

  @Provides
  @Singleton
  @Named("maxLimit")
  Integer provideMaxLimit() {
    return config.maxLimit();
  }

  @Provides
  @Singleton
  @Named("defaultOffset")
  Long provideDefaultOffset() {
    return config.defaultOffset();
  }

  @Provides
  @Singleton
  @Named("limitFieldName")
  String provideLimitFieldName() {
    return config.limitFieldName();
  }

  @Provides
  @Singleton
  @Named("offsetFieldName")
  String provideOffsetFieldName() {
    return config.offsetFieldName();
  }
}
