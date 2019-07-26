package com.cerner.beadledom.integration.service.resource;

import com.cerner.beadledom.integration.api.model.HelloWorldDto;
import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.jaxrs.GenericResponses;
import com.cerner.beadledom.pagination.OffsetPaginatedList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "/hello", description = "Retrieve hello world data")
@Path("/hello")
public class HelloWorldResourceImpl {

  @ApiOperation(
      value = "Retrieves hello world data.",
      response = HelloWorldDto.class)
  @ApiResponse(code = 200, message = "Success")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public GenericResponse<OffsetPaginatedList<HelloWorldDto>> getHelloWorld() {
    ArrayList<HelloWorldDto> items = new ArrayList<>();
    items.add(HelloWorldDto.create("Beadledom", "Hello World!"));
    OffsetPaginatedList<HelloWorldDto> body = OffsetPaginatedList.<HelloWorldDto>builder()
        .metadata(1L).items(items).build();
    return GenericResponses.ok(body).build();
  }
}
