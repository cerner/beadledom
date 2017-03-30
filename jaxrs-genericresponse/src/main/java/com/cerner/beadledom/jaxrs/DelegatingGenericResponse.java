package com.cerner.beadledom.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * A {@link GenericResponse} implementation that delegates to an underlying JAX-RS {@link Response}.
 *
 * @author John Leacox
 * @since 1.3
 */
public class DelegatingGenericResponse<T> extends Response implements GenericResponse<T> {
  private final T body;
  private final Class<?> bodyClass;
  private final ErrorBody errorBody;

  private final Response rawResponse;

  /**
   * This constructor exists so that {@link DelegatingGenericResponse} class can be extended.
   *
   * <p>{@link DelegatingGenericResponse} should not be instantiated directly.
   */
  protected DelegatingGenericResponse(T body, ErrorBody errorBody, Response rawResponse) {
    if (rawResponse == null) {
      throw new NullPointerException("rawResponse:null");
    }

    this.body = body;
    this.bodyClass = body != null ? body.getClass() : null;
    this.errorBody = errorBody;
    this.rawResponse = rawResponse;
  }

  /**
   * Creates a new {@link DelegatingGenericResponse}.
   *
   * <p>If the rawResponse status code is in the range [200..300) the {@link ErrorBody} will be
   * null. If the rawResponse status code is outside of the range [200..300) then a new
   * {@link ErrorBody} will be created from the rawResponse and associated to this generic
   * response.
   *
   * @param body the entity body of the response, may be {@code null} if no body is present
   * @param rawResponse the raw JAX-RS response to delegate to
   */
  public static <T> DelegatingGenericResponse<T> create(T body, Response rawResponse) {
    if (rawResponse == null) {
      throw new NullPointerException("rawResponse:null");
    }

    ErrorBody errorBody = null;
    int code = rawResponse.getStatus();
    if (code < 200 || code >= 300) {
      errorBody = ErrorBody.fromResponse(rawResponse);
    }

    return new DelegatingGenericResponse<T>(body, errorBody, rawResponse);
  }

  @Override
  public boolean isSuccessful() {
    int statusCode = rawResponse.getStatus();
    return statusCode >= 200 && statusCode < 300;
  }

  @Override
  public T body() {
    return body;
  }

  @Override
  public ErrorBody errorBody() {
    return errorBody;
  }

  /**
   * Null will be returned if there is no body present.
   */
  public Class<?> getEntityClass() {
    return bodyClass;
  }

  /**
   * Null will be returned if there is no body present.
   */
  public Type getGenericType() {
    return bodyClass;
  }

  @Override
  public int getStatus() {
    return rawResponse.getStatus();
  }

  @Override
  public Response.StatusType getStatusInfo() {
    return rawResponse.getStatusInfo();
  }

  @Override
  public Object getEntity() {
    return rawResponse.getEntity();
  }

  @Override
  public <U> U readEntity(Class<U> entityType) {
    if (!entityType.equals(bodyClass)) {
      throw new IllegalArgumentException("entityType is not the same type as the body entity");
    }

    return rawResponse.readEntity(entityType);
  }

  @Override
  public <U> U readEntity(GenericType<U> entityType) {
    if (!entityType.getRawType().equals(bodyClass)) {
      throw new IllegalArgumentException(
          "Raw generic type is not the same type as the body entity");
    }

    return rawResponse.readEntity(entityType);
  }

  @Override
  public <U> U readEntity(Class<U> entityType, Annotation[] annotations) {
    if (!entityType.equals(bodyClass)) {
      throw new IllegalArgumentException("entityType is not the same type as the body entity");
    }

    return rawResponse.readEntity(entityType, annotations);
  }

  @Override
  public <U> U readEntity(GenericType<U> entityType, Annotation[] annotations) {
    if (!entityType.getRawType().equals(bodyClass)) {
      throw new IllegalArgumentException(
          "Raw generic type is not the same type as the body entity");
    }

    return rawResponse.readEntity(entityType, annotations);
  }

  @Override
  public boolean hasEntity() {
    return rawResponse.hasEntity();
  }

  @Override
  public boolean bufferEntity() {
    return rawResponse.bufferEntity();
  }

  @Override
  public void close() {
    rawResponse.close();
  }

  @Override
  public MediaType getMediaType() {
    return rawResponse.getMediaType();
  }

  @Override
  public Locale getLanguage() {
    return rawResponse.getLanguage();
  }

  @Override
  public int getLength() {
    return rawResponse.getLength();
  }

  @Override
  public Set<String> getAllowedMethods() {
    return rawResponse.getAllowedMethods();
  }

  @Override
  public Map<String, NewCookie> getCookies() {
    return rawResponse.getCookies();
  }

  @Override
  public EntityTag getEntityTag() {
    return rawResponse.getEntityTag();
  }

  @Override
  public Date getDate() {
    return rawResponse.getDate();
  }

  @Override
  public Date getLastModified() {
    return rawResponse.getLastModified();
  }

  @Override
  public URI getLocation() {
    return rawResponse.getLocation();
  }

  @Override
  public Set<Link> getLinks() {
    return rawResponse.getLinks();
  }

  @Override
  public boolean hasLink(String relation) {
    return rawResponse.hasLink(relation);
  }

  @Override
  public Link getLink(String relation) {
    return rawResponse.getLink(relation);
  }

  @Override
  public Link.Builder getLinkBuilder(String relation) {
    return rawResponse.getLinkBuilder(relation);
  }

  @Override
  public MultivaluedMap<String, Object> getMetadata() {
    return rawResponse.getMetadata();
  }

  @Override
  public MultivaluedMap<String, Object> getHeaders() {
    return rawResponse.getHeaders();
  }

  @Override
  public MultivaluedMap<String, String> getStringHeaders() {
    return rawResponse.getStringHeaders();
  }

  @Override
  public String getHeaderString(String name) {
    return rawResponse.getHeaderString(name);
  }

  @Override
  public String toString() {
    return "DelegatingGenericResponse{body=" + body + ", bodyClass=" + bodyClass + ", errorBody=" + errorBody
            + ", rawResponse=Response{status=" + rawResponse.getStatus() + ", mediaType="
            + rawResponse.getMediaType() + ", date=" + rawResponse.getDate() + ", length=" + rawResponse.getLength()
            + ", lastModified=" + rawResponse.getLastModified() + ", entityTag=" + rawResponse.getEntityTag()
            + ", language=" + rawResponse.getLanguage() + ", location=" + rawResponse.getLocation()
            + ", headers=" + rawResponse.getHeaders() + ", cookies=" + rawResponse.getCookies() + ", links="
            + rawResponse.getLinks() + " } }";
  }
}
