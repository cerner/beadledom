package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.BeadledomClientBuilderFactory;

/**
 * Factory implementation of {@link BeadledomClientBuilderFactory}.
 *
 * @author Sundeep Paruvu
 * @since 2.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public class ResteasyClientBuilderFactory implements BeadledomClientBuilderFactory {

  @Override
  public BeadledomResteasyClientBuilder create() {
    return BeadledomResteasyClientBuilder.newBuilder();
  }
}
