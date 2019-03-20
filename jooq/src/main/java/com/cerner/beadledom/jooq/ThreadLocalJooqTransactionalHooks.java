package com.cerner.beadledom.jooq;

/**
 * @author John Leacox
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
