package com.cerner.beadledom.jooq;

/**
 * @author John Leacox
 */
class ThreadLocalJooqTransactionalHooks extends JooqTransactionalHooks {
  private ThreadLocal<JooqTransaction> transactions = new ThreadLocal<>();

  @Override
  public void whenCommitted(Runnable action) {
    JooqTransaction transaction = transactions.get();
    if (transaction != null) {
      transaction.addCommitHook(action);
    }

    // TODO: What if there isn't a transaction context currently? Throw exception? Run immediately?
    //   Maybe run immediately and log a message?
  }

  void setCurrentTransaction(JooqTransaction transaction) {
    transactions.set(transaction);
  }

  void clearTransaction() {
    transactions.remove();
  }
}
