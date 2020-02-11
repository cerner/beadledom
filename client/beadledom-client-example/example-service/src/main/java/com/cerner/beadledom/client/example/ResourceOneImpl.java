package com.cerner.beadledom.client.example;

import com.cerner.beadledom.client.example.model.JsonOne;
import com.cerner.beadledom.jaxrs.PATCH;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Service implementation of resource one.
 *
 * @author John Leacox
 */
@Path("/one")
public class ResourceOneImpl {
  @GET
  @Produces("application/json")
  public JsonOne get() {
    return JsonOne.create("One", "Hello World");
  }

  @POST
  @Produces("application/json")
  @Consumes("application/json")
  public JsonOne echo(JsonOne json) {
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
  public JsonOne patch(JsonOne json) {
    return JsonOne.builder()
        .setHello("Hola1")
        .setOne("New Json")
        .build();
  }
}
