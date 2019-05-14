package com.cerner.beadledom.jaxrs.provider;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class ForwardedHeaderFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String httpScheme = requestIsSecure(requestContext) ? "https" : "http";
    requestContext.setRequestUri(requestContext.getUriInfo().getBaseUriBuilder().scheme(httpScheme).build());
  }

  /**
   * Checks if the request was made from a secure context or not.
   * @param requestContext The {@link ContainerRequestContext} object containing the container request headers.
   * @return true if the request was made from a secure context, false otherwise.
   */
  private boolean requestIsSecure(ContainerRequestContext requestContext) {
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
    MultivaluedMap<String, String> headerMap = requestContext.getHeaders();
    String forwardedHeader = headerMap.getFirst("Forwarded");
    Pattern forwardedPairs = Pattern.compile("proto=(?<protocolValue>[^;]*)(;|\\z)");

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
    return "https".equals(requestContext.getHeaders().getFirst("X-Forwarded-Proto"));
  }
}
