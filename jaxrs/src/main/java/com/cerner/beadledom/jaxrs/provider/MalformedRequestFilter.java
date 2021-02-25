package com.cerner.beadledom.jaxrs.provider;

import com.cerner.beadledom.json.common.model.JsonError;
import java.net.URI;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * The MalformedRequestFilter reads the request context and determines if the request has a valid uri structure if it
 * does not then it aborts the request and responds with a 400
 *
 * @author Kyle Roush
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class MalformedRequestFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) {
    try {
      URI.create(requestContext.getUriInfo().getAbsolutePath().toString());
    } catch (IllegalArgumentException e) {
      requestContext.abortWith(
              Response.status(400)
                      .type(MediaType.APPLICATION_JSON)
                      .entity(JsonError.builder().code(400).message("Malformed URI").build())
                      .build());
    }
  }
}
