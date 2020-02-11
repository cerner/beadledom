package com.cerner.beadledom.client.example;

import com.cerner.beadledom.client.example.model.JsonTwo;
import com.cerner.beadledom.jaxrs.PATCH;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Service implementation of resource two.
 *
 * @author John Leacox
 */
@Path("/two")
public class ResourceTwoImpl {
  @GET
  @Produces("application/json")
  public JsonTwo get() {
    return JsonTwo.create("Two", "Hello World");
  }

  @POST
  @Produces("application/json")
  @Consumes("application/json")
  public JsonTwo echo(JsonTwo json) {
    return json;
  }

  /**
   * Patch Resource.
   *
   * @param json JSON patch
   * @return The updated representation
   */
  @PATCH
  @Produces("application/json")
  @Consumes("application/json")
  public JsonTwo patch(JsonTwo json) {
    return JsonTwo.builder()
        .setHello("Hola2")
        .setTwo("New Json")
        .build();
  }
}
