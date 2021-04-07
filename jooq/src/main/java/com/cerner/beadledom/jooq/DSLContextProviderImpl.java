package com.cerner.beadledom.jooq;

import javax.inject.Inject;
import javax.sql.DataSource;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.ThreadLocalTransactionProvider;

/**
 * An implementation of {@link DSLContextProvider} and {@link UnitOfWork} that creates a Jooq
 * {@link DSLContext} and scopes the unit of work to the current thread.
 *
 * @author John Leacox
 * @since 2.7
 */
class DSLContextProviderImpl implements DSLContextProvider, UnitOfWork {
  private final Configuration configuration;
  private final ThreadLocal<DSLContext> dslContexts = new ThreadLocal<>();

  @Inject
  DSLContextProviderImpl(
      DataSource dataSource, SQLDialect sqlDialect,
      ThreadLocalJooqTransactionalHooks transactionalHooks) {
    // ThreadLocalTransactionProvider and ThreadLocalCommitHookExecutingTransactionListener handle
    // all of the ThreadLocal semantics of Jooq and Transaction hooks for us.
    this.configuration = new DefaultConfiguration()
        .set(sqlDialect)
        .set(new ThreadLocalTransactionProvider(new DataSourceConnectionProvider(dataSource), false))
        .set(new ThreadLocalCommitHookExecutingTransactionListener(transactionalHooks));
  }

  @Override
  public DSLContext get() {
    DSLContext dslContext = dslContexts.get();
    if (dslContext != null) {
      return dslContext;
    } else {
      throw new IllegalStateException("UnitOfWork is not running.");
    }
  }

  @Override
  public void begin() {
    if (isActive()) {
      throw new IllegalStateException("UnitOfWork has already been started.");
    } else {
      DSLContext dslContext = DSL.using(configuration);
      dslContexts.set(dslContext);
    }
  }

  @Override
  public boolean isActive() {
    return dslContexts.get() != null;
  }

  @Override
  public void end() {
    DSLContext dslContext = dslContexts.get();
    if (dslContext != null) {
      dslContexts.remove();
    }
  }
}
