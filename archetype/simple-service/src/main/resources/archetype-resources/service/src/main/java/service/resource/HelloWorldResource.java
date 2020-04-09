#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.resource;

import ${package}.api.model.HelloWorldDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/hello", description = "Retrieve hello world data")
@Path("/hello")
public class HelloWorldResource {

  @ApiOperation(
      value = "Retrieves hello world data.",
      response = HelloWorldDto.class)
  @ApiResponse(code = 200, message = "Success")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHelloWorld() {
    return Response
        .ok(HelloWorldDto.create("Beadledom", "Hello World!"))
        .build();
  }
}
