package com.cerner.beadledom.pagination;

import com.cerner.beadledom.pagination.models.OffsetPaginationConfiguration;
import com.cerner.beadledom.pagination.parameters.LimitParameter;
import com.cerner.beadledom.pagination.parameters.OffsetParameter;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;

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
  @Override
  protected void configure() {
    // provide default configuration
    OptionalBinder
        .newOptionalBinder(
            binder(),
            OffsetPaginationConfiguration.class)
        .setDefault().toInstance(OffsetPaginationConfiguration.builder().build());

    requestStaticInjection(LimitParameter.class);
    requestStaticInjection(OffsetParameter.class);

    bind(OffsetPaginatedListLinksWriterInterceptor.class);
  }
}
