package com.cerner.beadledom.swagger2;

import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.listing.BaseApiListingResource;
import io.swagger.models.Swagger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * This provides the swagger 2 documentation at the /api-docs path that was previously used for
 * swagger 1.
 */
@Path("/api-docs")
public class SwaggerApiResource extends BaseApiListingResource {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "The swagger definition in JSON", hidden = true)
  public Response getListingJson(
      @Context Application app,
      @Context ServletContext context,
      @Context ServletConfig servletConfig,
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo) {
    Swagger swagger = process(app, context, servletConfig, headers, uriInfo);
    if (swagger == null) {
      throw new NotFoundException();
    }

    swagger.basePath(context.getContextPath());
    return Response.ok().entity(swagger).type(MediaType.APPLICATION_JSON).build();
  }
}
