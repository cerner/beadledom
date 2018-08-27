package com.cerner.beadledom.client.proxy;

import com.cerner.beadledom.jaxrs.GenericResponse;

import javax.ws.rs.core.Response;

/**
 * A JAX-RS resource for testing with generic response.
 *
 * @author John Leacox
 */
public interface GenericTestingResource {
  GenericResponse<String> getGeneric();

  GenericResponse<String> getGenericWithParameter(String input);

  GenericResponse getGenericWithNoType();

  Response getStandard();
}
