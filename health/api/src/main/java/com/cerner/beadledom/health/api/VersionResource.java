package com.cerner.beadledom.health.api;

import com.cerner.beadledom.health.dto.BuildDto;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

@Api(value = "/health",
    description = "Application Version")
@io.swagger.annotations.Api(value = "/health",
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
  @io.swagger.annotations.ApiOperation(value = "Application Version Information",
      notes = "The version information of the Application.",
      response = BuildDto.class)
  @io.swagger.annotations.ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 503, message = "Service Unavailable"),
      @io.swagger.annotations.ApiResponse(code = 200, message = "Success")})
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  BuildDto getVersionInfo();
}
