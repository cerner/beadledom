package com.cerner.beadledom.jooq;

import java.util.ArrayDeque;
import java.util.Deque;
import org.jooq.TransactionContext;
import org.jooq.impl.DefaultTransactionListener;

/**
 * // TODO: Better class name
 * @author John Leacox
 */
class ThreadLocalCommitHookExecutingTransactionListener extends DefaultTransactionListener {
  private final ThreadLocal<Deque<JooqTransaction>> transactionDeques = new ThreadLocal<>();

  private final ThreadLocalJooqTransactionalHooks transactionalHooks;

  ThreadLocalCommitHookExecutingTransactionListener(
      ThreadLocalJooqTransactionalHooks transactionalHooks) {
    this.transactionalHooks = transactionalHooks;
  }

  @Override
  public void beginEnd(TransactionContext ctx) {
    Deque<JooqTransaction> transactions = transactions();

    JooqTransaction transaction = new JooqTransaction();
    // TODO: Is ThreadLocal required to know we are at the root or can we use
    //  TransactionContext.transaction to track the current transaction? Alternatively
    //  we could use ctx.configuration().data() to store the Deque
    // TODO: Does relying on a Deque always give us the matching transaction?
    //  I think so; Jooq uses a Deque for savepoints
    //ctx.transaction(transaction);
    transactions.push(transaction);
    transactionalHooks.setCurrentTransaction(transaction);
  }

  private Deque<JooqTransaction> transactions() {
    Deque<JooqTransaction> transactions = transactionDeques.get();

    if (transactions == null) {
      transactions = new ArrayDeque<>();
      transactionDeques.set(transactions);
    }

    return transactions;
  }

  @Override
  public void commitEnd(TransactionContext ctx) {
    Deque<JooqTransaction> transactions = transactions();
    JooqTransaction transaction = transactions.pop();

    if (transactions.isEmpty()) {
      executeCommitHooks(transaction);

      transactionalHooks.clearTransaction();
      transactionDeques.remove();
    }
  }

  private void executeCommitHooks(JooqTransaction transaction) {
    for (Runnable commitHook : transaction.getCommitHooks()) {
      // TODO: What if an exception occurs?
      commitHook.run();
    }

    for (JooqTransaction nested : transaction.getNestedTransactions()) {
      executeCommitHooks(nested);
    }
  }

  @Override
  public void rollbackEnd(TransactionContext ctx) {
    Deque<JooqTransaction> transactions = transactions();

    JooqTransaction transaction = transactions.pop();

    transaction.clearCommitHooks();
    for (JooqTransaction nested : transaction.getNestedTransactions()) {
      nested.clearCommitHooks();
    }

    if (transactions.isEmpty()) {
      transactionalHooks.clearTransaction();
      transactionDeques.remove();
    }
  }
}
