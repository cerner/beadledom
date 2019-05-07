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
    System.out.println("Base URI: " + uriInfo.getBaseUri().toString());
    System.out.println("Request URI: " + uriInfo.getRequestUri().toString());
    System.out.println("Request Headers: ");
    httpHeaders.getRequestHeaders().forEach((key, value) -> System.out.println("Header Key: "+ key + ", Header value: "+ value));
    String httpScheme = isRequestSecure(securityContext, httpHeaders) ? "https" : "http";
    return StreamingWriterOutput.with(writer -> MUSTACHE_FACTORY.compile("ui.mustache").execute(
        writer, ImmutableMap.of(
            "apiDocsUrl",
            uriInfo.getBaseUriBuilder().scheme(httpScheme).path(SwaggerApiResource.class).build().toASCIIString()))
    );
  }

  private boolean isRequestSecure(SecurityContext securityContext, HttpHeaders httpHeaders) {
    return securityContext.isSecure()
        || secureForwardedHeader(httpHeaders.getRequestHeaders().getFirst("Forwarded"))
        || secureXForwardedProtoHeader(httpHeaders.getRequestHeaders().getFirst("X-Forwarded-Proto"));
  }

  private boolean secureForwardedHeader(String forwardedHeader) {
    Pattern forwardedPairs = Pattern.compile("proto=(?<protocolValue>[^;]*)(;|\\z)");

    return forwardedHeader != null
        && forwardedPairs.matcher(forwardedHeader).group("protocolValue").contains("https");
  }

  private boolean secureXForwardedProtoHeader(String xForwardedProtoHeader) {
    return xForwardedProtoHeader != null && xForwardedProtoHeader.equals("https");
  }
}

