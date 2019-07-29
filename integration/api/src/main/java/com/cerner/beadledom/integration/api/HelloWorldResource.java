package com.cerner.beadledom.integration.api;

import com.cerner.beadledom.integration.api.model.HelloWorldDto;
import com.cerner.beadledom.integration.api.model.OffsetPaginatedListHelloWorldDto;
import com.cerner.beadledom.jaxrs.GenericResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Client interface of an offset based paginated list endpoint resource.
 *
 * @author Nick Behrens
 */
@Api(value = "/hello", description = "Retrieve hello world data")
@Path("/hello")
public interface HelloWorldResource {

  /**
   * A endpoint that returns a paginated list of {@link HelloWorldDto}.
   *
   * @return an {@link OffsetPaginatedListHelloWorldDto}
   */
  @ApiOperation(
      value = "Retrieves hello world data.",
      response = HelloWorldDto.class)
  @ApiResponse(code = 200, message = "Success")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  GenericResponse<OffsetPaginatedListHelloWorldDto> getHelloWorld();
}
