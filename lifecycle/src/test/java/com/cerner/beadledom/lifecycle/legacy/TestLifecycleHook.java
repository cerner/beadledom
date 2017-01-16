package com.cerner.beadledom.lifecycle.legacy;

import javax.annotation.PreDestroy;

/**
 * @author John Leacox
 */
class TestLifecycleHook {
  boolean hasExecutedShutdown = false;

  @PreDestroy
  void shutdown() {
    hasExecutedShutdown = true;
  }
}
