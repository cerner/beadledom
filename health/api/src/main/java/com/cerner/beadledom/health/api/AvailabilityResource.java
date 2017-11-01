package com.cerner.beadledom.health.api;

import com.cerner.beadledom.health.dto.HealthDto;
import com.cerner.beadledom.health.dto.HealthJsonViews;
import com.fasterxml.jackson.annotation.JsonView;
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
    description = "Health and dependency checks")
@io.swagger.annotations.Api(value = "/health",
    description = "Health and dependency checks")
@Path("meta/availability")
public interface AvailabilityResource {
  @GET
  @Produces(MediaType.TEXT_HTML)
  StreamingOutput getBasicAvailabilityCheckHtml();

  @ApiOperation(value = "Basic Availability Check",
      notes = "Always returns 200. The JSON will only include the message field.",
      response = HealthDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 503, message = "unhealthy"),
      @ApiResponse(code = 200, message = "healthy")})
  @io.swagger.annotations.ApiOperation(value = "Basic Availability Check",
      notes = "Always returns 200. The JSON will only include the message field.",
      response = HealthDto.class)
  @io.swagger.annotations.ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 503, message = "unhealthy"),
      @io.swagger.annotations.ApiResponse(code = 200, message = "healthy")})
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(HealthJsonViews.Availability.class)
  HealthDto getBasicAvailabilityCheck();
}
