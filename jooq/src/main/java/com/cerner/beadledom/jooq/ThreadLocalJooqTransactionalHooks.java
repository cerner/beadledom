package com.cerner.beadledom.jooq;

import java.util.Objects;

/**
 * An implementation of {@link JooqTransactionalHooks} that uses {@link ThreadLocal} to track the
 * current transaction nesting.
 *
 * @author John Leacox
 * @since 3.3
 */
class ThreadLocalJooqTransactionalHooks implements JooqTransactionalHooks {
  private ThreadLocal<JooqTransaction> transactions = new ThreadLocal<>();

  @Override
  public void whenCommitted(Runnable action) {
    Objects.requireNonNull(action);

    JooqTransaction transaction = transactions.get();

    if (transaction == null) {
      action.run();
      return;
    }

    transaction.addCommitHook(action);
  }

  void setTransaction(JooqTransaction transaction) {
    transactions.set(transaction);
  }

  void clearTransaction() {
    transactions.remove();
  }
}
