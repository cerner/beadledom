package com.cerner.beadledom.client.example.client;

import com.cerner.beadledom.client.example.model.JsonTwo;
import com.cerner.beadledom.jaxrs.PATCH;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Example rest resource two for beadledom-client.
 *
 * @author John Leacox
 */
@Path("/two")
public interface ResourceTwo {
  @GET
  @Produces("application/json")
  JsonTwo get();

  @POST
  @Produces("application/json")
  @Consumes("application/json")
  JsonTwo echo(JsonTwo json);

  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  JsonTwo patch(JsonTwo json);
}
