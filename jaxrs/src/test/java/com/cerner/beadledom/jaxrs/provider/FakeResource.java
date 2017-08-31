package com.cerner.beadledom.jaxrs.provider;

import com.cerner.beadledom.jaxrs.PATCH;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/fakeResource")
public class FakeResource {

  private final FakeDao fakeDao;

  public FakeResource() {
    fakeDao = new FakeDao();
  }

  public FakeResource(FakeDao fakeDao) {
    this.fakeDao = fakeDao;
  }

  @PATCH
  @Path("/Patch")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response fakePatch(FakeModel model) {
    model.setId("newId");
    model.setName("newName");
    return Response.ok(model).build();
  }

  @GET
  @Path("/ExceptionEndpoint")
  @Produces({MediaType.APPLICATION_JSON})
  public Response fakeExceptionEndpoint() {
    return Response.ok(fakeDao.fakeMethod()).build();
  }
}
