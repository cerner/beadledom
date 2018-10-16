package com.cerner.beadledom.swagger2;

import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

@Path("/meta/swagger")
public class SwaggerUiResource {
  private static final String WEBJAR_PATH = "/META-INF/resources/webjars/swagger-ui/3.19.0/";
  private static final MustacheFactory MUSTACHE_FACTORY = new DefaultMustacheFactory(
      "com/cerner/beadledom/swagger2");

  private static final Set<String> ASSETS = ImmutableSet.of(
      "favicon-16x16.png",
      "favicon-32x32.png",
      "swagger-ui.css",
      "swagger-ui-bundle.js",
      "swagger-ui-standalone-preset.js"
  );

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

  // This is a terrible way to serve static assets, but this shouldn't see a lot of traffic.
  //
  // This is served at /meta/swagger, one level above the UI HTML page /meta/swagger/ui, so that the hard-coded
  // relative link to "images/throbber.gif" in swagger-ui.json will work.
  @GET
  @Path("/{asset: .+}")
  public Response getSwaggerUiAsset(@PathParam("asset") String assetPath) throws IOException {
    if (!ASSETS.contains(assetPath)) {
      return Response.status(404).build();
    }

    StreamingOutput streamingOutput = new StreamingOutput() {
      @Override
      public void write(OutputStream outputStream) throws IOException, WebApplicationException {
        try (InputStream inputStream = getClass().getResourceAsStream(WEBJAR_PATH + assetPath)) {
          int nextByte;
          while ((nextByte = inputStream.read()) != -1) {
            outputStream.write(nextByte);
          }
          outputStream.flush();
          outputStream.close();
        }
      }
    };
    return Response.ok(streamingOutput).build();
  }
}
