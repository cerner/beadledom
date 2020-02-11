package com.cerner.beadledom.client.example.client;

import com.cerner.beadledom.client.example.model.JsonOne;
import com.cerner.beadledom.jaxrs.PATCH;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Example rest resource one for beadledom-client.
 *
 * @author John Leacox
 */
@Path("/one")
public interface ResourceOne {
  @GET
  @Produces("application/json")
  JsonOne get();

  @POST
  @Produces("application/json")
  @Consumes("application/json")
  JsonOne echo(JsonOne json);

  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  JsonOne patch(JsonOne json);
}
