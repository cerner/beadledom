package com.cerner.beadledom.jooq;

import java.util.ArrayList;
import java.util.List;
import org.jooq.Transaction;

/**
 * TODO: Come up with a better name for this class
 * @author John Leacox
 */
class JooqTransaction implements Transaction {
  private final List<Runnable> commitHooks = new ArrayList<>();
  private final List<JooqTransaction> nestedTransactions = new ArrayList<>();

  public void addCommitHook(Runnable action) {
    commitHooks.add(action);
  }

  public List<Runnable> getCommitHooks() {
    return commitHooks;
  }

  public void addNestedTransaction(JooqTransaction nested) {
    nestedTransactions.add(nested);
  }

  public List<JooqTransaction> getNestedTransactions() {
    return nestedTransactions;
  }

  public void clearCommitHooks() {
    commitHooks.clear();
  }
}
