package com.cerner.beadledom.health.api;

import com.cerner.beadledom.health.dto.BuildDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

@Api(value = "/health",
    description = "Application Version")
@Path("meta/version")
public interface VersionResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  StreamingOutput getVersionInfoHtml();

  @ApiOperation(value = "Application Version Information",
      notes = "The version information of the Application.",
      response = BuildDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 503, message = "Service Unavailable"),
      @ApiResponse(code = 200, message = "Success")})
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  BuildDto getVersionInfo();
}
