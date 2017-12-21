package com.cerner.beadledom.health.api;

import com.cerner.beadledom.health.dto.BuildDto;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
      notes = "Always returns 200. The version information of the Application.",
      response = BuildDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 503, message = "unhealthy"),
      @ApiResponse(code = 200, message = "healthy")})
  @Operation(summary = "The version information of the Application.",
      description = "The response JSON will contain a message and the results of checking the "
          + "health of primary dependencies, although stack traces will be excluded.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "healthy",
              content = @Content(schema = @Schema(implementation = BuildDto.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "503",
              description = "unhealthy")
      }
  )
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  BuildDto getVersionInfo();
}
