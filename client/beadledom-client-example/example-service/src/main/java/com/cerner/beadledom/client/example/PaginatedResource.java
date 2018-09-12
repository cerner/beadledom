package com.cerner.beadledom.client.example;

import com.cerner.beadledom.client.example.model.JsonOne;
import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.pagination.OffsetPaginatedList;
import com.cerner.beadledom.pagination.parameters.OffsetPaginationParameters;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Example paginated resource.
 *
 * @author Ian Kottman
 */
@Path("/paginated")
public interface PaginatedResource {
  @GET
  @Produces("application/json")
  GenericResponse<OffsetPaginatedList<JsonOne>> index(@BeanParam OffsetPaginationParameters paginationParams);
}
