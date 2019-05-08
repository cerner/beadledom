package com.cerner.beadledom.swagger;

import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

@Path("/meta/swagger")
public class SwaggerUiResource {
  private static final MustacheFactory MUSTACHE_FACTORY = new DefaultMustacheFactory(
      "com/cerner/beadledom/swagger");

  private static final Set<String> ASSETS = ImmutableSet.of(
      "css/reset.css",
      "css/screen.css",
      "images/explorer_icons.png",
      "images/throbber.gif",
      "lib/backbone-min.js",
      "lib/handlebars-1.0.0.js",
      "lib/highlight.7.3.pack.js",
      "lib/jquery-1.8.0.min.js",
      "lib/jquery.ba-bbq.min.js",
      "lib/jquery.slideto.min.js",
      "lib/jquery.wiggle.min.js",
      "lib/shred.bundle.js",
      "lib/swagger.js",
      "lib/underscore-min.js",
      "lib/shred/content.js",
      "swagger-ui.js"
  );

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

  // This is a terrible way to serve static assets, but this shouldn't see a lot of traffic.
  //
  // This is served at /meta/swagger, one level above the UI HTML page /meta/swagger/ui, so that the hard-coded
  // relative link to "images/throbber.gif" in swagger-ui.json will work.
  @GET
  @Path("/{asset: .+}")
  public Response getSwaggerUiAsset(@PathParam("asset") String assetPath) {
    if (!ASSETS.contains(assetPath)) {
      return Response.status(404).build();
    }

    InputStream stream = getClass().getResourceAsStream("ui-dist/" + assetPath);
    return Response.ok(stream).build();
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
