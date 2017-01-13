package com.cerner.beadledom.resteasy.fauxservice.resource;

import com.cerner.beadledom.resteasy.fauxservice.dao.HelloDao;
import com.cerner.beadledom.resteasy.fauxservice.fauxmodels.PointlessThing;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/hello", description = "A test resource.")
@Path("/hello")
public class HelloResource {
  private final HelloDao helloDao;

  @Inject
  public HelloResource(HelloDao helloDao) {
    this.helloDao = helloDao;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHello() {
    return Response.ok(helloDao.getHello()).build();
  }

  @ApiOperation(value = "echo",
      response = PointlessThing.class
  )
  @Path("/echo")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postEcho(@ApiParam(required = true) PointlessThing input) {
    return Response.ok(PointlessThing.newBuilder().setText(input.getText().toLowerCase()).build())
        .build();
  }
}
