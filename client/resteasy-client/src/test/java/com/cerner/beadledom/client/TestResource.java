package com.cerner.beadledom.client;

import com.cerner.beadledom.jaxrs.GenericResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
public interface TestResource {
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  String get();

  @GET
  @Path("error")
  @Produces(MediaType.TEXT_PLAIN)
  String error();

  @POST
  @Path("data")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  String getData(String data);

  @GET
  @Path("loopyGetCorrelationId")
  @Produces(MediaType.TEXT_PLAIN)
  String loopyGetCorrelationId();

  @GET
  @Path("echo-correlation-id")
  @Produces(MediaType.TEXT_PLAIN)
  String echoCorrelationId();

  @GET
  @Path("json")
  @Produces(MediaType.APPLICATION_JSON)
  JsonModel getJson();

  @GET
  @Path("responseJson")
  @Produces(MediaType.APPLICATION_JSON)
  Response getResponseJson();

  @GET
  @Path("genericResponseJson")
  @Produces(MediaType.APPLICATION_JSON)
  GenericResponse<JsonModel> getGenericResponseJson();

  @GET
  @Path("genericResponseJsonWithRetries")
  @Produces(MediaType.APPLICATION_JSON)
  GenericResponse<JsonModel> getGenericResponseJsonWithRetries();

  @GET
  @Path("responseError")
  @Produces({MediaType.APPLICATION_JSON})
  Response getResponseError();

  @GET
  @Path("genericResponseError")
  @Produces({MediaType.APPLICATION_JSON})
  GenericResponse<JsonModel> getGenericResponseError();

  @GET
  @Path("genericResponseJsonError")
  @Produces(MediaType.APPLICATION_JSON)
  GenericResponse<String> getGenericResponseJsonError();
}
