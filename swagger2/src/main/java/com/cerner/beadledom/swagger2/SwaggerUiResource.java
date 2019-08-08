package com.cerner.beadledom.swagger2;

import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

@Path("/meta/swagger")
public class SwaggerUiResource {
  private static final MustacheFactory MUSTACHE_FACTORY = new DefaultMustacheFactory(
      "com/cerner/beadledom/swagger2");

  @GET
  @Path("/ui")
  @Produces(MediaType.TEXT_HTML)
  public StreamingOutput getSwaggerUi(@Context UriInfo uriInfo) {
    return StreamingWriterOutput.with(writer -> MUSTACHE_FACTORY.compile("ui.mustache").execute(
        writer, ImmutableMap.of(
            "apiDocsUrl",
            uriInfo.getBaseUriBuilder().path(SwaggerApiResource.class).build().toASCIIString()))
    );
  }
}
