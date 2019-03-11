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

  void addCommitHook(Runnable action) {
    commitHooks.add(action);
  }

  List<Runnable> getCommitHooks() {
    return commitHooks;
  }

  void addNestedTransaction(JooqTransaction nested) {
    nestedTransactions.add(nested);
  }

  List<JooqTransaction> getNestedTransactions() {
    return nestedTransactions;
  }

  void clearCommitHooks() {
    commitHooks.clear();
  }
}
