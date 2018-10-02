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
import javax.ws.rs.core.Response;

@Api(value = "/health", description = "Health and dependency checks")
@io.swagger.annotations.Api(value = "/health", description = "Health and dependency checks")
@Path("meta/health")
public interface HealthResource {
  @GET
  @Produces(MediaType.TEXT_HTML)
  Response getPrimaryHealthCheckHtml();

  @ApiOperation(value = "Primary Health Check",
      notes =
          "The response JSON will contain a message and the results of checking the health of "
              + "primary dependencies, although stack traces will be excluded.",
      response = HealthDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 503, message = "unhealthy", response = HealthDto.class),
      @ApiResponse(code = 200, message = "healthy", response = HealthDto.class)})
  @io.swagger.annotations.ApiOperation(value = "Primary Health Check",
      notes =
          "The response JSON will contain a message and the results of checking the health of "
              + "primary dependencies, although stack traces will be excluded.",
      response = HealthDto.class)
  @io.swagger.annotations.ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 503, message = "unhealthy",
          response = HealthDto.class),
      @io.swagger.annotations.ApiResponse(code = 200, message = "healthy",
          response = HealthDto.class)})
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(HealthJsonViews.Primary.class)
  Response getPrimaryHealthCheck();
}
