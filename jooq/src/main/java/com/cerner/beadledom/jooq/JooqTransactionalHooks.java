package com.cerner.beadledom.jooq;

/**
 * Transaction callback hooks to be executed when the current Jooq transaction completes.
 *
 * @author John Leacox
 * @since 3.3
 */
public abstract class JooqTransactionalHooks {
  /**
   * Registers an action to be executed when the current Jooq transaction is successfully committed.
   *
   * <p>
   * If this method is called while a Jooq transaction is not active, the action will be executed
   * immediately.
   *
   * <p>
   * If the current Jooq transaction is rolled back instead of committed, then the action will be
   * discarded and never called.
   *
   * <h3>Savepoints / Nested Transactions</h3>
   * <p>
   * Actions registered on nested transaction (or savepoints) will be called after the top-level
   * transaction is committed, but not if a rollback to the savepoint or any parent savepoint
   * occurred during the transaction. When a savepoint is rolled back the commit hooks associated
   * with that savepoint or further nested savepoints will not be executed. However, commit hooks
   * associated with savepoints at a higher level of nesting than the rollback, will be executed if
   * the top-level transaction commits.
   *
   * <h3>Order of Execution</h3>
   * <p>
   * The callback actions will be executed in the order they are registered.
   *
   * <h3>Exception Handling</h3>
   * <p>
   * If a callback action throws an exception, no later registered callbacks in the same transaction
   * will be executed.
   *
   * @param action the action to be executed upon the successful commit of the current Jooq
   *     transaction
   */
  public abstract void whenCommitted(Runnable action);

  abstract void setTransaction(JooqTransaction transaction);

  abstract void clearTransaction();
}
