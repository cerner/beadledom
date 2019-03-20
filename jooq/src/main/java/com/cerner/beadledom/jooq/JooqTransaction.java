package com.cerner.beadledom.jooq;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a Jooq Transaction with post-commit hooks and nested transactions.
 *
 * @author John Leacox
 */
class JooqTransaction {
  private final List<Runnable> commitHooks = new ArrayList<>();
  private final List<JooqTransaction> nestedContexts = new ArrayList<>();

  void addCommitHook(Runnable action) {
    commitHooks.add(action);
  }

  List<Runnable> getCommitHooks() {
    return commitHooks;
  }

  void addNestedTransaction(JooqTransaction nested) {
    nestedContexts.add(nested);
  }

  List<JooqTransaction> getNestedTransactions() {
    return nestedContexts;
  }

  void clearCommitHooks() {
    commitHooks.clear();
  }
}
