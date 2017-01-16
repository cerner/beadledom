package com.cerner.beadledom.jaxrs;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

/**
 * A class that contains most of the static utility methods for {@link GenericResponse}
 * corresponding to the utility methods from the {@link Response} class.
 *
 * <p>This utility class is needed because Java 6 does not allow static methods on interfaces.
 *
 * @author John Leacox
 * @since 1.3
 */
public class GenericResponses {

  private static final GenericResponseBuilderFactory genericResponseBuilderFactory =
      GenericResponseBuilderFactory.newInstance();

  private GenericResponses() {
  }

  /**
   * Creates a new {@link GenericResponseBuilder} by performing a shallow copy of the existing
   * response.
   *
   * @param response the {@link GenericResponse} to create a new builder from.
   * @see Response.ResponseBuilder#fromResponse(Response)
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> fromResponse(GenericResponse<T> response) {
    GenericResponseBuilder<T> builder =
        genericResponseBuilderFactory.create(response.getStatus(), response.body());

    for (String headerName : response.getHeaders().keySet()) {
      List<Object> headerValues = response.getHeaders().get(headerName);
      for (Object headerValue : headerValues) {
        builder.header(headerName, headerValue);
      }
    }
    return builder;
  }

  /**
   * Creates a new {@link GenericResponseBuilder} with the given HTTP status.
   *
   * @param status the response status.
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> status(Response.StatusType status) {
    if (status == null) {
      throw new NullPointerException("status:null");
    }

    return status(status.getStatusCode());
  }

  /**
   * Creates a new {@link GenericResponseBuilder} with the given HTTP status.
   *
   * @param status the response status.
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> status(Response.Status status) {
    return status((Response.StatusType) status);
  }

  /**
   * Creates a new {@link GenericResponseBuilder} with the given HTTP status.
   *
   * @param status the response status.
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> status(int status) {
    return genericResponseBuilderFactory.create(status);
  }

  /**
   * Creates a new {@link GenericResponseBuilder} with the given HTTP status and entity body.
   *
   * @param status the response status
   * @param entity the response entity
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> status(int status, T entity) {
    return genericResponseBuilderFactory.create(status, entity);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with an OK status.
   *
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> ok() {
    return status(Response.Status.OK);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with an OK status and the given entity body.
   *
   * @param entity the response entity
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> ok(T entity) {
    return genericResponseBuilderFactory.create(Response.Status.OK.getStatusCode(), entity);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with an OK status and the given entity body and
   * media type.
   *
   * @param entity the response entity
   * @param type the response entity media type
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> ok(T entity, MediaType type) {
    return ok(entity).type(type);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with an OK status and the given entity body and
   * media type.
   *
   * @param entity the response entity
   * @param type the response entity media type
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> ok(T entity, String type) {
    return ok(entity).type(type);
  }

  /**
   /**
   * Create a new {@link GenericResponseBuilder} with an OK status and the given entity body and
   * variant data.
   *
   * <p>The variant data includes the content type, language, and encoding.
   *
   * @param entity the response entity
   * @param variant the variant data
   * @return a new builder
   * @see Variant
   */
  public static <T> GenericResponseBuilder<T> ok(T entity, Variant variant) {
    return ok(entity).variant(variant);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with a server error status.
   */
  public static <T> GenericResponseBuilder<T> serverError() {
    return status(Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with a created status and the given entity body and
   * location.
   *
   * @param entity the response entity
   * @param location the URI of the newly created resource
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> created(T entity, URI location) {
    return genericResponseBuilderFactory.create(Response.Status.CREATED.getStatusCode(), entity)
        .location(location);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with an ACCEPTED status.
   *
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> accepted() {
    return status(Response.Status.ACCEPTED);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with a ACCEPTED status and the given entity body.
   *
   * @param entity the response entity
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> accepted(T entity) {
    return genericResponseBuilderFactory.create(Response.Status.ACCEPTED.getStatusCode(), entity);
  }

  /**
   * Create a new {@link GenericResponseBuilder} for an empty response.
   *
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> noContent() {
    return status(Response.Status.NO_CONTENT);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with a not-modified status.
   *
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> notModified() {
    return status(Response.Status.NOT_MODIFIED);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with a not-modified status.
   *
   * @param tag the entity tag of the unmodified entity
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> notModified(EntityTag tag) {
    return GenericResponses.<T>notModified().tag(tag);
  }

  /**
   * Create a new {@link GenericResponseBuilder} with a not-modified status.
   *
   * @param tag the entity tag of the unmodified entity
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> notModified(String tag) {
    return GenericResponses.<T>notModified().tag(tag);
  }

  /**
   * Create a new {@link GenericResponseBuilder} for a redirection.
   *
   * @param location the redirection URI
   * @return a new builder
   * @see Response.ResponseBuilder#seeOther(URI)
   */
  public static <T> GenericResponseBuilder<T> seeOther(URI location) {
    return GenericResponses.<T>status(Response.Status.SEE_OTHER).location(location);
  }

  /**
   * Create a new {@link GenericResponseBuilder} for a temporary redirection.
   *
   * @param location the redirection URI
   * @return a new builder
   * @see Response.ResponseBuilder#temporaryRedirect(URI)
   */
  public static <T> GenericResponseBuilder<T> temporaryRedirect(URI location) {
    return GenericResponses.<T>status(Response.Status.TEMPORARY_REDIRECT).location(location);
  }

  /**
   * Create a new {@link GenericResponseBuilder} for a not acceptable response.
   *
   * @param variants list of variants that were available
   * @return a new builder
   */
  public static <T> GenericResponseBuilder<T> notAcceptable(List<Variant> variants) {
    return GenericResponses.<T>status(Response.Status.NOT_ACCEPTABLE).variants(variants);
  }
}
