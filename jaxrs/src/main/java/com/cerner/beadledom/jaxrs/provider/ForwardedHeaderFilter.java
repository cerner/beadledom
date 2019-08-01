package com.cerner.beadledom.jaxrs.provider;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * The ForwardedHeaderFilter reads the request context and determines if the client request was made
 * from a secure protocol (HTTPS). If the request originated from secure context, it updates the
 * request to reflect the original protocol.
 *
 * <p>To determine if the client request made in a secure context was forwarded by a load balancer
 * or proxy, the "Forwarded" and "X-Forwarded-Proto" header values are extracted. If either of them
 * show the request was made from a secure context, the internal request object is updated to reflect
 * the original secure protocol.</p>
 *
 * @author Nick Behrens
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
@PreMatching
public class ForwardedHeaderFilter implements ContainerRequestFilter {
  // This regex takes the value of the 'proto=' field and sets it to a group. The 'proto=' field can
  // be followed by either another key=value pair separated by a ; or it could be the end of the header
  // Examples:
  // proto=https => 'protocolValue' group is set to "https"
  // proto=http;host=localhost => 'protocolValue' group is set to "http"
  Pattern forwardedPairs = Pattern.compile("proto=(?<protocolValue>[^;]*)(;|\\z)");

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String httpScheme = isRequestSecure(requestContext) ? "https" : "http";
    requestContext.setRequestUri(
        requestContext.getUriInfo().getRequestUriBuilder().scheme(httpScheme).build());
  }

  /**
   * Checks if the request was made from a secure context or not.
   * @param requestContext The {@link ContainerRequestContext} object containing the container request headers.
   * @return true if the request was made from a secure context, false otherwise.
   */
  private boolean isRequestSecure(ContainerRequestContext requestContext) {
    return requestContext.getSecurityContext().isSecure()
        || hasSecureForwardedHeader(requestContext)
        || hasSecureXForwardedProtoHeader(requestContext);
  }

  /**
   * Checks the Forwarded header for a protocol value of https.
   * @param requestContext The {@link ContainerRequestContext} object containing the container request headers.
   * @return true if the `Forwarded` header is present and contains https; false otherwise.
   */
  private boolean hasSecureForwardedHeader(ContainerRequestContext requestContext) {
    String forwardedHeader = requestContext.getHeaderString("Forwarded");
    if (forwardedHeader == null) {
      return false;
    }

    Matcher matcher = forwardedPairs.matcher(forwardedHeader);

    return matcher.find()
        && matcher.group("protocolValue").contains("https");
  }

  /**
   * Checks the X-Forwarded-Proto header for a value of https.
   * @param requestContext The {@link ContainerRequestContext} object containing the container request headers.
   * @return true if the `X-Forwarded-Proto` header is present and contains https; false otherwise.
   */
  private boolean hasSecureXForwardedProtoHeader(ContainerRequestContext requestContext) {
    return "https".equals(requestContext.getHeaderString("X-Forwarded-Proto"));
  }
}
