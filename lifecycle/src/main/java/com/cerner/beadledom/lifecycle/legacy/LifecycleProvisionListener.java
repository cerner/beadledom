package com.cerner.beadledom.lifecycle.legacy;

import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.spi.ProvisionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A Guice provision listener, that looks for {@link PostConstruct} and {@link PreDestroy}
 * annotations and executes them at startup or notifies the shutdown manager to execute them at
 * shutdown.
 *
 * @author John Leacox
 * @since 1.2
 * @deprecated The Governator lifecycle implementation should be used instead if possible
 */
@Singleton
@Deprecated
class LifecycleProvisionListener implements ProvisionListener {
  private LifecycleShutdownManager shutdownManager;

  LifecycleProvisionListener() {
  }

  /**
   * Initializes the provision listener with a shutdown manager from Guice.
   *
   * <p>This method must use static injection to guarantee this injection occurs before other
   * injections.
   */
  @Inject
  static void init(
      LifecycleShutdownManager shutdownManager, LifecycleProvisionListener listener) {
    listener.shutdownManager = shutdownManager;
  }

  @Override
  public <T> void onProvision(ProvisionInvocation<T> provision) {
    final Key<?> key = provision.getBinding().getKey();
    final Class<?> clazz = key.getTypeLiteral().getRawType();

    final T injectee = provision.provision();

    // Skip the shutdown manager since it's circular and doesn't have any lifecycle methods
    if (injectee instanceof LifecycleShutdownManager
        || injectee instanceof LifecycleProvisionListener) {
      return;
    }

    List<InvokableLifecycleMethod> postConstructMethods = findPostConstructMethods(injectee, clazz);
    List<InvokableLifecycleMethod> preDestroyMethods = findPreDestroyMethods(injectee, clazz);

    for (InvokableLifecycleMethod postConstructMethod : postConstructMethods) {
      try {
        postConstructMethod.invoke();
      } catch (Exception e) {
        throw new ProvisionException("Fail to provision object of type " + key, e);
      }
    }

    if (!preDestroyMethods.isEmpty()) {
      shutdownManager.addPreDestroyMethods(preDestroyMethods);
    }
  }

  private <T> List<InvokableLifecycleMethod> findPostConstructMethods(T injectee, Class<?> type) {
    List<InvokableLifecycleMethod> postConstructMethods = new ArrayList<InvokableLifecycleMethod>();
    for (Method method : getAllMethods(type)) {
      if (method.isAnnotationPresent(PostConstruct.class)) {
        postConstructMethods
            .add(new InvokableLifecycleMethodImpl(injectee, method, PostConstruct.class));
      }
    }

    return postConstructMethods;
  }

  private <T> List<InvokableLifecycleMethod> findPreDestroyMethods(T injectee, Class<?> type) {
    List<InvokableLifecycleMethod> preDestroyMethods = new ArrayList<InvokableLifecycleMethod>();
    for (Method method : getAllMethods(type)) {
      if (method.isAnnotationPresent(PreDestroy.class)) {
        preDestroyMethods.add(new InvokableLifecycleMethodImpl(injectee, method, PreDestroy.class));
      }
    }

    return preDestroyMethods;
  }

  private List<Method> getAllMethods(Class<?> type) {
    List<Method> allMethods = new ArrayList<Method>();
    Class<?> clazz = type;
    while (clazz != null) {
      allMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
      clazz = clazz.getSuperclass();
    }

    return allMethods;
  }
}
