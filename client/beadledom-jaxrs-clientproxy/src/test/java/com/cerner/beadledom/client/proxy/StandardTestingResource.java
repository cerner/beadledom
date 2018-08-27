package com.cerner.beadledom.client.proxy;

import javax.ws.rs.core.Response;

/**
 * A JAX-RS resource for testing with standard response.
 *
 * @author John Leacox
 */
public interface StandardTestingResource {
  Response getGeneric();

  Response getGenericWithNoType();

  Response getStandard();
}
