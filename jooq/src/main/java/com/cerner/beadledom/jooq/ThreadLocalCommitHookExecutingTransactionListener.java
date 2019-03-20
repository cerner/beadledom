package com.cerner.beadledom.jooq;

import java.util.ArrayDeque;
import java.util.Deque;
import org.jooq.TransactionContext;
import org.jooq.TransactionListener;
import org.jooq.impl.DefaultTransactionListener;

/**
 * An implementation of {@link TransactionListener} sets the current transaction for
 * {@link JooqTransactionalHooks} and removing hooks on rollback or executing commit hooks when the
 * top-level transaction is committed.
 *
 * @author John Leacox
 * @since 3.3
 */
class ThreadLocalCommitHookExecutingTransactionListener extends DefaultTransactionListener {
  private final ThreadLocal<Deque<JooqTransaction>> transactionDeques = new ThreadLocal<>();

  // This assumes that the hooks implementation is using ThreadLocal or some other means to have
  // the same lifecycle as this listener.
  private final JooqTransactionalHooks transactionalHooks;

  ThreadLocalCommitHookExecutingTransactionListener(JooqTransactionalHooks transactionalHooks) {
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
    for (JooqTransaction nested : transaction.getNestedTransactions()) {
      // TODO: Do we need to recurse all the way down? I suspect no, if we remove direct children,
      //   we also remove the further nested children due to transitivity.
      nested.clearCommitHooks();
    }

    // This is the top-level transaction
    if (transactions.isEmpty()) {
      clear();
    }
  }

  /**
   * Returns the Deque of nested transactions associated to the current thread.
   */
  private Deque<JooqTransaction> transactions() {
    // TODO: Does relying on a Deque always give us the matching transaction?
    //  I think so; Jooq uses a Deque for savepoints
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
   * If a commit hook throws an exception, the commit hook execution chain will be stopped
   * immediately, and no later registered commit hooks in that same transaction will be executed.
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
   * Clears the nested transactions Deque and transaction hooks.
   */
  private void clear() {
    transactionalHooks.clearTransaction();
    transactions().remove();
  }
}
