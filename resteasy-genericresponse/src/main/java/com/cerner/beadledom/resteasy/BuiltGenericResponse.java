package com.cerner.beadledom.resteasy;

import com.cerner.beadledom.jaxrs.ErrorBody;
import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.jaxrs.ResponseToStringWrapper;
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
import org.jboss.resteasy.specimpl.BuiltResponse;

/**
 * An extension of the resteasy {@link BuiltResponse} class with additional implementations for
 * {@link GenericResponse}.
 *
 * <p>Consumers should avoid depending on this class directly instead preferring to depend on the
 * {@link GenericResponse} or {@link Response} types. This class is necessary for the internal
 * workings of Resteasy because Resteasy is heavily dependent on the {@link BuiltResponse} class.
 *
 * @author John Leacox
 * @since 1.3
 * @see BuiltResponse
 * @see GenericResponse
 */
public class BuiltGenericResponse<T> extends BuiltResponse implements GenericResponse<T> {
  private final T body;
  private final Class<?> bodyClass;

  private final ErrorBody errorBody;
  private final Response rawResponse;

  protected BuiltGenericResponse(T body, ErrorBody errorBody, Response rawResponse) {
    this.body = body;
    this.bodyClass = body != null ? body.getClass() : null;
    this.errorBody = errorBody;
    this.rawResponse = rawResponse;
  }

  /**
   * Creates a new instance of {@link BuiltGenericResponse} with the specified body and underlying
   * rawResponse.
   *
   * @param body the response entity body
   * @param rawResponse the underlying raw response
   */
  public static <T> BuiltGenericResponse<T> create(T body, Response rawResponse) {
    ErrorBody errorBody = null;
    int code = rawResponse.getStatus();
    if (code < 200 || code >= 300) {
      errorBody = ErrorBody.fromResponse(rawResponse);
    }

    return new BuiltGenericResponse<T>(body, errorBody, rawResponse);
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

  @Override
  public Class<?> getEntityClass() {
    if (bodyClass != null) {
      return bodyClass;
    }

    if (errorBody != null && rawResponse instanceof BuiltResponse) {
      return ((BuiltResponse) rawResponse).getEntityClass();
    }

    return null;
  }

  @Override
  public Type getGenericType() {
    if (bodyClass != null) {
      return bodyClass;
    }

    if (errorBody != null && rawResponse instanceof BuiltResponse) {
      return ((BuiltResponse) rawResponse).getGenericType();
    }

    return null;
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
  public MultivaluedMap<String, String> getStringHeaders() {
    return rawResponse.getStringHeaders();
  }

  @Override
  public MultivaluedMap<String, Object> getHeaders() {
    return rawResponse.getHeaders();
  }

  @Override
  public String getHeaderString(String name) {
    return rawResponse.getHeaderString(name);
  }

  @Override
  public String toString() {
    return "BuiltGenericResponse{body=" + body + ", bodyClass=" + bodyClass + ", errorBody=" + errorBody
            + ", rawResponse=" + new ResponseToStringWrapper(rawResponse) + " }";
  }
}
