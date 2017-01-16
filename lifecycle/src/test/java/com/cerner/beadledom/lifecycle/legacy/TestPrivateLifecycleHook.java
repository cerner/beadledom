package com.cerner.beadledom.lifecycle.legacy;

import javax.annotation.PreDestroy;

/**
 * @author John Leacox
 */
class TestPrivateLifecycleHook {
  boolean hasExecutedShutdown = false;

  @PreDestroy
  private void shutdown() {
    hasExecutedShutdown = true;
  }
}
