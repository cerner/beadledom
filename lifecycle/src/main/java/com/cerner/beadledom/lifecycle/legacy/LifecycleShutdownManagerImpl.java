package com.cerner.beadledom.lifecycle.legacy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link LifecycleShutdownManager}.
 *
 * @author John Leacox
 * @since 1.2
 * @deprecated The Governator lifecycle implementation should be used instead if possible
 */
@Singleton
@Deprecated
class LifecycleShutdownManagerImpl implements LifecycleShutdownManager {
  private static final Logger logger = LoggerFactory.getLogger(LifecycleShutdownManagerImpl.class);

  public LifecycleShutdownManagerImpl() {
  }

  private Set<InvokableLifecycleMethod> preDestroyMethods = new HashSet<InvokableLifecycleMethod>();

  @Override
  public synchronized void addPreDestroyMethods(List<InvokableLifecycleMethod> preDestroyMethods) {
    this.preDestroyMethods.addAll(preDestroyMethods);
  }

  @Override
  public synchronized void shutdown() {
    for (InvokableLifecycleMethod preDestroyMethod : preDestroyMethods) {
      try {
        preDestroyMethod.invoke();
      } catch (Exception e) {
        logger.error("Fail to shutdown object", e);
      }
    }
  }
}
