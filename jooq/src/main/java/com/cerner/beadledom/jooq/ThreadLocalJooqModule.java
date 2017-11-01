package com.cerner.beadledom.jooq;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import com.google.inject.AbstractModule;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.jooq.SQLDialect;

/**
 * A guice module that configures the Jooq DSLContext and Jooq Transactions.
 *
 * <p>The configured DSLContexts and transactions will be scoped by thread. This means that this
 * module should only be used if it is guaranteed that database transactions and contexts will
 * never cross thread boundaries.
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link DSLContextProvider}</li>
 *     <li>{@link JooqTransactional} AOP interceptor</li>
 * </ul>
 *
 * @author John Leacox
 * @since 2.7
 */
public class ThreadLocalJooqModule extends AbstractModule {
  @Override
  protected void configure() {
    requireBinding(DataSource.class);
    requireBinding(SQLDialect.class);

    bind(DSLContextProviderImpl.class).in(Singleton.class);
    bind(DSLContextProvider.class).to(DSLContextProviderImpl.class);
    bind(UnitOfWork.class).to(DSLContextProviderImpl.class);

    final JooqTxnInterceptor txnInterceptor = new JooqTxnInterceptor();
    requestInjection(txnInterceptor);

    bindInterceptor(any(), annotatedWith(JooqTransactional.class), txnInterceptor);
    bindInterceptor(annotatedWith(JooqTransactional.class), any(), txnInterceptor);
  }
}
