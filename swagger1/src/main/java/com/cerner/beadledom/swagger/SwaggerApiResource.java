package com.cerner.beadledom.swagger;

import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.model.ApiListing;
import javax.servlet.ServletConfig;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * This alters the responses of Swagger's ApiListingResourceJSON class so that the basePath field
 * will be set.
 *
 * <p>Normally you must set the basePath globally on the SwaggerConfig, but doing it like this
 * allows us to retrieve the path from the UriInfo rather than explicitly configuring it.
 *
 * <p>If the basePath is already set this class will not alter it.
 */
@Path("/api-docs")
public class SwaggerApiResource extends ApiListingResourceJSON {
  @Override
  public Response apiDeclaration(
      @PathParam("route") String route,
      @Context Application app,
      @Context ServletConfig sc,
      @Context HttpHeaders headers,
      @Context UriInfo uriInfo) {
    Response original = super.apiDeclaration(route, app, sc, headers, uriInfo);

    if (original.getEntity() == null || !(original.getEntity() instanceof ApiListing)) {
      return original;
    }

    ApiListing listing = (ApiListing) original.getEntity();
    if (listing.basePath() != null) {
      return original;
    }

    return Response.fromResponse(original)
        .entity(
            new ApiListing(
                listing.apiVersion(),
                listing.swaggerVersion(),
                uriInfo.getBaseUri().toString().replaceAll("/\\z", ""),
                listing.resourcePath(),
                listing.produces(),
                listing.consumes(),
                listing.protocols(),
                listing.authorizations(),
                listing.apis(),
                listing.models(),
                listing.description(),
                listing.position()
            )).build();
  }
}
