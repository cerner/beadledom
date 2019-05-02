package com.cerner.beadledom.jooq;

/**
 * An implementation of {@link JooqTransactionalHooks} that uses {@link ThreadLocal} to track the
 * current transaction nesting.
 *
 * @author John Leacox
 * @since 3.3
 */
class ThreadLocalJooqTransactionalHooks extends JooqTransactionalHooks {
  private ThreadLocal<JooqTransaction> transactions = new ThreadLocal<>();

  @Override
  public void whenCommitted(Runnable action) {
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
