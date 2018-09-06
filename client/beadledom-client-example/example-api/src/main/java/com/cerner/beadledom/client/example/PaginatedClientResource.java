package com.cerner.beadledom.client.example;

import com.cerner.beadledom.client.example.model.JsonOne;
import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Proxy client interface for PaginatedResource.
 *
 * @author Ian Kottman
 */
@Path("/paginated")
public interface PaginatedClientResource {
  @GET
  @Produces("application/json")
  GenericResponse<OffsetPaginatedListDto<JsonOne>> index(
      @QueryParam("offset") final Long offset,
      @QueryParam("limit") final Integer limit);
}
