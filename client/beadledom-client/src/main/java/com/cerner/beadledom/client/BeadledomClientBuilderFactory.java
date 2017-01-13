package com.cerner.beadledom.client;

/**
 * Factory to create the BeadledomClientBuilder.
 *
 *<p>The implementations of this factory should only be used when a specific
 * {@link BeadledomClientBuilder} implementation is required. If the consumer doesn't care about the
 * implementation then the consumer should use {@link BeadledomClientBuilder#newBuilder()}
 *
 * @author Sundeep Paruvu
 * @since 2.0
 */
public interface BeadledomClientBuilderFactory {

  /**
   * Creates a new instance of {@link BeadledomClientBuilder}.
   */
  BeadledomClientBuilder create();
}
