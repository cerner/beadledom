package com.cerner.beadledom.jooq;

import java.util.ArrayDeque;
import java.util.Deque;
import org.jooq.TransactionContext;
import org.jooq.TransactionListener;
import org.jooq.impl.DefaultTransactionListener;

/**
 * An implementation of {@link TransactionListener} that sets the current transaction for
 * {@link JooqTransactionalHooks} and removes hooks on rollback or executes commit hooks when the
 * top-level transaction is committed.
 *
 * @author John Leacox
 * @since 3.3
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
    JooqTransaction transaction = new JooqTransaction();

    Deque<JooqTransaction> transactions = transactions();
    JooqTransaction parentTransaction = transactions.peekLast();
    if (parentTransaction != null) {
      parentTransaction.addNestedTransaction(transaction);
    }

    // Jooq provides the ability to store a custom transaction on the context via ctx#transaction(),
    // however this doesn't provide the ability to know when a transaction event is being called
    // on the top-level transaction vs nested transactions. Since we only want to run commit hooks
    // when the top-level transaction is committed, we must track the transaction nesting level
    // ourselves.
    transactions.push(transaction);
    transactionalHooks.setTransaction(transaction);
  }

  @Override
  public void commitEnd(TransactionContext ctx) {
    Deque<JooqTransaction> transactions = transactions();
    JooqTransaction transaction = transactions.pop();

    // This is the top-level transaction
    if (transactions.isEmpty()) {
      try {
        executeCommitHooks(transaction);
      } finally {
        clear();
      }
    }
  }

  @Override
  public void rollbackEnd(TransactionContext ctx) {
    Deque<JooqTransaction> transactions = transactions();
    JooqTransaction transaction = transactions.pop();

    transaction.clearCommitHooks();
    transaction.clearNestedTransaction();

    // This is the top-level transaction
    if (transactions.isEmpty()) {
      clear();
    }
  }

  /**
   * Returns the Deque of nested transactions associated to the current thread.
   */
  private Deque<JooqTransaction> transactions() {
    Deque<JooqTransaction> result = transactionDeques.get();

    if (result == null) {
      result = new ArrayDeque<>();
      transactionDeques.set(result);
    }

    return result;
  }

  /**
   * Executes the commit hooks associated with the transaction and all nested transaction hooks.
   *
   * <p>
   * If a commit hook throws an exception, the commit hook execution chain will be stopped
   * immediately, and no later registered commit hooks in that same transaction will be executed.
   * </p>
   *
   * @param transaction the transaction to execute commit hooks for.
   */
  private void executeCommitHooks(JooqTransaction transaction) {
    for (Runnable commitHook : transaction.getCommitHooks()) {
      commitHook.run();
    }

    for (JooqTransaction nested : transaction.getNestedTransactions()) {
      executeCommitHooks(nested);
    }
  }

  /**
   * Clears the nested transaction hooks and transactions Deque.
   */
  private void clear() {
    transactionalHooks.clearTransaction();
    transactionDeques.remove();
  }
}
