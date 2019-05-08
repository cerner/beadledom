package com.cerner.beadledom.swagger2;

import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import java.util.regex.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

@Path("/meta/swagger")
public class SwaggerUiResource {
  private static final MustacheFactory MUSTACHE_FACTORY = new DefaultMustacheFactory(
      "com/cerner/beadledom/swagger2");

  @GET
  @Path("/ui")
  @Produces(MediaType.TEXT_HTML)
  public StreamingOutput getSwaggerUi(@Context UriInfo uriInfo,
      @Context SecurityContext securityContext, @Context HttpHeaders httpHeaders) {
    String httpScheme = isRequestSecure(securityContext, httpHeaders) ? "https" : "http";
    return StreamingWriterOutput.with(writer -> MUSTACHE_FACTORY.compile("ui.mustache").execute(
        writer, ImmutableMap.of(
            "apiDocsUrl",
            uriInfo.getBaseUriBuilder().scheme(httpScheme).path(SwaggerApiResource.class).build().toASCIIString()))
    );
  }

  /**
   * Checks if the request was made from a secure context or not.
   * @param securityContext The {@link SecurityContext} object that could validate security of the request.
   * @param httpHeaders The {@link HttpHeaders} object containing the request headers.
   * @return true if the request was made from a secure context, false otherwise.
   */
  private boolean isRequestSecure(SecurityContext securityContext, HttpHeaders httpHeaders) {
    return securityContext.isSecure()
        || hasSecureForwardedHeader(httpHeaders)
        || hasSecureXForwardedProtoHeader(httpHeaders);
  }

  /**
   * Checks the Forwarded header for a protocol value of https.
   * @param httpHeaders The {@link HttpHeaders} object containing the request headers.
   * @return true if the `Forwarded` header is present and contains https; false otherwise.
   */
  private boolean hasSecureForwardedHeader(HttpHeaders httpHeaders) {
    String forwardedHeader = httpHeaders.getRequestHeaders().getFirst("Forwarded");
    Pattern forwardedPairs = Pattern.compile("proto=(?<protocolValue>[^;]*)(;|\\z)");

    return forwardedHeader != null
        && forwardedPairs.matcher(forwardedHeader).group("protocolValue").contains("https");
  }

  /**
   * Checks the X-Forwarded-Proto header for a value of https.
   * @param httpHeaders The {@link HttpHeaders} object containing the request headers.
   * @return true if the `X-Forwarded-Proto` header is present and contains https; false otherwise.
   */
  private boolean hasSecureXForwardedProtoHeader(HttpHeaders httpHeaders) {
    return "https".equals(httpHeaders.getRequestHeaders().getFirst("X-Forwarded-Proto"));
  }
}
