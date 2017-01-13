package com.cerner.beadledom.client;

import java.lang.annotation.Annotation;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lifecycle hooks for {@link BeadledomClient}.
 *
 * <p>This makes sure that the Beadledom client is closed when the lifecycle managing container
 * shuts down.
 *
 * @author John Leacox
 * @since 2.0
 */
class BeadledomClientLifecycleHook {
  private static final Logger logger = LoggerFactory.getLogger(BeadledomClientLifecycleHook.class);

  private final BeadledomClient beadledomClient;
  private final Class<? extends Annotation> annotation;

  BeadledomClientLifecycleHook(
      BeadledomClient beadledomClient, Class<? extends Annotation> annotation) {
    this.beadledomClient = beadledomClient;
    this.annotation = annotation;
  }

  @PostConstruct
  public void postConstruct() {
  }

  @PreDestroy
  public void preDestroy() {
    try {
      beadledomClient.close();
    } catch (Exception e) {
      logger.warn("Error shutting down client annotated with {}", annotation, e);
    }
  }
}
