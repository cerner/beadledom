package com.cerner.beadledom.integration.client;

import com.cerner.beadledom.integration.api.model.HelloWorldDto;
import com.cerner.beadledom.integration.api.model.OffsetPaginatedListHelloWorldDto;
import com.cerner.beadledom.jaxrs.GenericResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Client interface of an offset based paginated list endpoint resource.
 *
 * @author Nick Behrens
 */
@Path("/hello")
public interface HelloWorldResource {

  /**
   * A endpoint that returns a paginated list of {@link HelloWorldDto}.
   *
   * @return an {@link OffsetPaginatedListHelloWorldDto}
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  GenericResponse<OffsetPaginatedListHelloWorldDto> getHelloWorld();
}
