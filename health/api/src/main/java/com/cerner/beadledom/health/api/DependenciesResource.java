package com.cerner.beadledom.health.api;

import com.cerner.beadledom.health.dto.HealthDependencyDto;
import com.cerner.beadledom.health.dto.HealthJsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Api(value = "/health",
    description = "Lists health check dependencies")
@io.swagger.annotations.Api(value = "/health",
    description = "Lists health check dependencies")
@Path("meta/health/diagnostic/dependencies")
public interface DependenciesResource {
  @GET
  @Produces(MediaType.TEXT_HTML)
  StreamingOutput getDependencyListingHtml();

  @ApiOperation(value = "Dependency Listing",
      notes = "Returns the name, internal URL and external URL (if applicable) of all "
          + "dependencies.",
      response = HealthDependencyDto.class,
      responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = 503, message = "unhealthy", response = HealthDependencyDto.class),
      @ApiResponse(code = 200, message = "healthy", response = HealthDependencyDto.class)})
  @io.swagger.annotations.ApiOperation(value = "Dependency Listing",
      notes = "Returns the name, internal URL and external URL (if applicable) of all "
          + "dependencies.",
      response = HealthDependencyDto.class,
      responseContainer = "List")
  @io.swagger.annotations.ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 503, message = "unhealthy",
          response = HealthDependencyDto.class),
      @io.swagger.annotations.ApiResponse(code = 200, message = "healthy",
          response = HealthDependencyDto.class)})
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(HealthJsonViews.Dependency.class)
  List<HealthDependencyDto> getDependencyListing();

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/{name}")
  Response getDependencyAvailabilityCheckHtml(@PathParam("name") String name);

  @ApiOperation(value = "Availability Check for Dependency",
      notes =
          "Invokes the basic availability check on the given dependency. "
              + "The response code will match the code returned by the dependency, and will be "
              + "omitted from the JSON.",
      response = HealthDependencyDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 503, message = "unhealthy", response = HealthDependencyDto.class),
      @ApiResponse(code = 200, message = "healthy", response = HealthDependencyDto.class)})
  @io.swagger.annotations.ApiOperation(value = "Availability Check for Dependency",
      notes =
          "Invokes the basic availability check on the given dependency. "
              + "The response code will match the code returned by the dependency, and will be "
              + "omitted from the JSON.",
      response = HealthDependencyDto.class)
  @io.swagger.annotations.ApiResponses(value = {
      @io.swagger.annotations.ApiResponse(code = 503, message = "unhealthy",
          response = HealthDependencyDto.class),
      @io.swagger.annotations.ApiResponse(code = 200, message = "healthy",
          response = HealthDependencyDto.class)})
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(HealthJsonViews.Dependency.class)
  @Path("/{name}")
  Response getDependencyAvailabilityCheck(@PathParam("name") String name);
}
