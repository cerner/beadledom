package com.cerner.beadledom.health.api;

import com.cerner.beadledom.health.dto.HealthDto;
import com.cerner.beadledom.health.dto.HealthJsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.StreamingOutput;

@Api(value = "/health",
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
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(HealthJsonViews.Availability.class)
  HealthDto getBasicAvailabilityCheck();
}
