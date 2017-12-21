package com.cerner.beadledom.openapi3;

import io.swagger.v3.core.filter.OpenAPISpecFilter;
import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JAX-RS resource for swagger 3 docs.
 *
 * @author John Leacox
 */
@Path("/meta/openapi3")
public class Swagger3ApiResource {
  private static final Logger logger = LoggerFactory.getLogger(Swagger3ApiResource.class);

  private final OpenApiContext context;

  @Inject
  Swagger3ApiResource(OpenApiContext context) {
    this.context = context;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(hidden = true)
  public Response getOpenApi(HttpHeaders headers, UriInfo uriInfo) throws Exception {
    OpenAPI oas = context.read();
    boolean pretty = false;
    if (context.getOpenApiConfiguration() != null && Boolean.TRUE
        .equals(context.getOpenApiConfiguration().isPrettyPrint())) {
      pretty = true;
    }

    if (oas != null) {
      if (context.getOpenApiConfiguration() != null
          && context.getOpenApiConfiguration().getFilterClass() != null) {
        try {
          OpenAPISpecFilter filterImpl =
              (OpenAPISpecFilter) Class.forName(context.getOpenApiConfiguration().getFilterClass())
                  .newInstance();
          SpecFilter f = new SpecFilter();
          oas = f.filter(oas, filterImpl, getQueryParams(uriInfo.getQueryParameters()),
              getCookies(headers),
              getHeaders(headers));
        } catch (Exception e) {
          logger.error("failed to load filter", e);
        }
      }
    }

    if (oas == null) {
      return Response.status(404).build();
    }

    if (StringUtils.isNotBlank("json") && "json".trim().equalsIgnoreCase("yaml")) {
      return Response.status(Response.Status.OK)
          .entity(pretty ? Yaml.pretty(oas) : Yaml.mapper().writeValueAsString(oas))
          .type("application/yaml")
          .build();
    } else {
      return Response.status(Response.Status.OK)
          .entity(pretty ? Json.pretty(oas) : Json.mapper().writeValueAsString(oas))
          .type(MediaType.APPLICATION_JSON_TYPE)
          .build();
    }
  }

  private static Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
    Map<String, List<String>> output = new HashMap<String, List<String>>();
    if (params != null) {
      for (String key : params.keySet()) {
        List<String> values = params.get(key);
        output.put(key, values);
      }
    }
    return output;
  }

  private static Map<String, String> getCookies(HttpHeaders headers) {
    Map<String, String> output = new HashMap<String, String>();
    if (headers != null) {
      for (String key : headers.getCookies().keySet()) {
        Cookie cookie = headers.getCookies().get(key);
        output.put(key, cookie.getValue());
      }
    }
    return output;
  }

  private static Map<String, List<String>> getHeaders(HttpHeaders headers) {
    Map<String, List<String>> output = new HashMap<String, List<String>>();
    if (headers != null) {
      for (String key : headers.getRequestHeaders().keySet()) {
        List<String> values = headers.getRequestHeaders().get(key);
        output.put(key, values);
      }
    }
    return output;
  }
}
