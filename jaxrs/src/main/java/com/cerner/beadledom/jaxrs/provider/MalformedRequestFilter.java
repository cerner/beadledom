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
 * The ForwardedHeaderFilter reads the request context and determines if the client request was made
 * from a secure protocol (HTTPS). If the request originated from secure context, it updates the
 * request to reflect the original protocol.
 *
 * <p>To determine if the client request made in a secure context was forwarded by a load balancer
 * or proxy, the "Forwarded" and "X-Forwarded-Proto" header values are extracted. If either of them
 * show the request was made from a secure context, the internal request object is updated to
 * reflect the original secure protocol.
 *
 * @author Nick Behrens
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class MalformedRequestFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) {
    try {
      URI.create(requestContext.getUriInfo().getAbsolutePath().toString());
    } catch (RuntimeException e) {
      requestContext.abortWith(
              Response.status(400)
                      .type(MediaType.APPLICATION_JSON)
                      .entity(JsonError.builder().code(400).message("Malformed URI").build())
                      .build());
    }
  }
}
