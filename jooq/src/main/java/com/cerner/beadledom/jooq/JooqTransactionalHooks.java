package com.cerner.beadledom.jooq;

/**
 * @author John Leacox
 */
public abstract class JooqTransactionalHooks {
  public abstract void whenCommitted(Runnable action);

  abstract void setCurrentTransaction(JooqTransaction transaction);
  abstract void clearTransaction();
}
