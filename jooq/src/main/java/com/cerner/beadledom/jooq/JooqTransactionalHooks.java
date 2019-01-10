package com.cerner.beadledom.jooq;

/**
 * @author John Leacox
 */
public interface JooqTransactionalHooks {
  void whenCommitted(Runnable action);
}
