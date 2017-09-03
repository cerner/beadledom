package com.cerner.beadledom.jaxrs;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * A class for building {@link GenericResponse} instances.
 *
 * <p>An initial instance of a builder can be obtained via the static methods of the
 * {@link GenericResponses} class.
 *
 * @author John Leacox
 * @since 1.3
 */
public abstract class GenericResponseBuilder<T> {
  private final Response.ResponseBuilder rawBuilder;

  private T body;
  private boolean hasErrorEntity = false;

  /**
   * Creates a new {@link GenericResponseBuilder} that delegates to the underlying
   * {@code rawBuilder}.
   */
  protected GenericResponseBuilder(Response.ResponseBuilder rawBuilder) {
    this.rawBuilder = rawBuilder;
  }

  /**
   * Return a new {@link GenericResponse} instance from the builder.
   */
  public GenericResponse<T> build() {
    return build(body, rawBuilder.build());
  }

  /**
   * Creates a new {@link GenericResponse} instance from the provided {@code body} and
   * {@code rawResponse}.
   *
   * @param body the entity body to build the {@link GenericResponse} with (may be null)
   * @param rawResponse the underlying raw response
   */
  protected abstract GenericResponse<T> build(T body, Response rawResponse);

  /**
   * Sets the HTTP status code on the builder.
   *
   * @param status the HTTP status code
   * @return this builder
   */
  public GenericResponseBuilder<T> status(int status) {
    rawBuilder.status(status);
    return this;
  }

  /**
   * Sets the HTTP status code on the builder.
   *
   * @param status the HTTP status code
   * @return this builder
   */
  public GenericResponseBuilder<T> status(Response.Status status) {
    rawBuilder.status(status);
    return this;
  }

  /**
   * Sets the HTTP status code on the builder.
   *
   * @param status the HTTP status code
   * @return this builder
   */
  public GenericResponseBuilder<T> status(Response.StatusType status) {
    rawBuilder.status(status);
    return this;
  }

  /**
   * Sets the response entity body on the builder.
   *
   * <p>A specific media type can be set using the {@code type(...)} methods.
   *
   * @param entity the response entity body
   * @return this builder
   * @see #type(MediaType)
   * @see #type(String)
   */
  public GenericResponseBuilder<T> entity(T entity) {
    if (hasErrorEntity) {
      throw new IllegalStateException(
          "errorEntity already set. Only one of entity and errorEntity may be set");
    }
    this.body = entity;
    rawBuilder.entity(entity);
    return this;
  }

  /**
   * Sets the response entity body on the builder.
   *
   * <p>A specific media type can be set using the {@code type(...)} methods.
   *
   * @param entity the response entity body
   * @param annotations annotations, in addition to the annotations on the resource method returning
   *     the response, to be passed to the {@link MessageBodyWriter}
   * @return this builder
   * @see #type(MediaType)
   * @see #type(String)
   */
  public GenericResponseBuilder<T> entity(T entity, Annotation[] annotations) {
    if (hasErrorEntity) {
      throw new IllegalStateException(
          "errorEntity already set. Only one of entity and errorEntity may be set");
    }
    this.body = entity;
    rawBuilder.entity(entity, annotations);
    return this;
  }

  /**
   * Sets the response error entity body on the builder.
   *
   * <p>A specific media type can be set using the {@code type(...)} methods.
   *
   * @param errorEntity the response error entity body
   * @return this builder
   * @see #type(MediaType)
   * @see #type(String)
   */
  public GenericResponseBuilder<T> errorEntity(Object errorEntity) {
    if (body != null) {
      throw new IllegalStateException(
          "entity already set. Only one of entity and errorEntity may be set");
    }
    rawBuilder.entity(errorEntity);
    hasErrorEntity = true;
    return this;
  }

  /**
   * Sets the response error entity body on the builder.
   *
   * <p>A specific media type can be set using the {@code type(...)} methods.
   *
   * @param errorEntity the response error entity body
   * @param annotations annotations, in addition to the annotations on the resource method returning
   *     the response, to be passed to the {@link MessageBodyWriter}
   * @return this builder
   * @see #type(MediaType)
   * @see #type(String)
   */
  public GenericResponseBuilder<T> errorEntity(Object errorEntity, Annotation[] annotations) {
    if (body != null) {
      throw new IllegalStateException(
          "entity already set. Only one of entity and errorEntity may be set");
    }
    rawBuilder.entity(errorEntity, annotations);
    hasErrorEntity = true;
    return this;
  }

  /**
   * Set the list of allowed HTTP methods (e.g. GET, POST, etc.) for the resource.
   *
   * @param methods the HTTP methods that will be listed as allowed for the resource, {@code null}
   *     will clear list
   * @return this builder
   */
  public GenericResponseBuilder<T> allow(String... methods) {
    rawBuilder.allow(methods);
    return this;
  }

  /**
   * Set the list of allowed HTTP methods (e.g. GET, POST, etc.) for the resource.
   *
   * @param methods the HTTP methods that will be listed as allowed for the resource, {@code null}
   *     will clear list
   * @return this builder
   */
  public GenericResponseBuilder<T> allow(Set<String> methods) {
    rawBuilder.allow(methods);
    return this;
  }

  /**
   * Sets the cache control data of the response.
   *
   * @param cacheControl the cache control data, {@code null} will clear any existing cache control
   *     data
   * @return this builder
   */
  public GenericResponseBuilder<T> cacheControl(CacheControl cacheControl) {
    rawBuilder.cacheControl(cacheControl);
    return this;
  }

  /**
   * Sets the encoding of the entity body.
   *
   * @param encoding the encoding of the entity body, {@code null} will clear the existing encoding
   * @return this builder
   */
  public GenericResponseBuilder<T> encoding(String encoding) {
    rawBuilder.encoding(encoding);
    return this;
  }

  /**
   * Adds a header to the response.
   *
   * @param name the header name
   * @param value the header value, {@code null} will clear any existing headers for the same name
   * @return this builder
   * @see Response.ResponseBuilder#header(String, Object)
   */
  public GenericResponseBuilder<T> header(String name, Object value) {
    rawBuilder.header(name, value);
    return this;
  }

  /**
   * Replaces all of the headers with the these headers.
   *
   * @param headers the new headers to be used, {@code null} to remove all existing headers
   * @return this builder
   */
  public GenericResponseBuilder<T> replaceAll(MultivaluedMap<String, Object> headers) {
    rawBuilder.replaceAll(headers);
    return this;
  }

  /**
   * Sets the response language.
   *
   * @param language the language of the response, {@code null} will clear the language
   * @return this builder
   */
  public GenericResponseBuilder<T> language(String language) {
    rawBuilder.language(language);
    return this;
  }

  /**
   * Sets the response language.
   *
   * @param language the language of the response, {@code null} will clear the language
   * @return this builder
   */
  public GenericResponseBuilder<T> language(Locale language) {
    rawBuilder.language(language);
    return this;
  }

  /**
   * Sets the response entity media type.
   *
   * @param type the response entity media type, {@code null} will clear the media type
   * @return this builder
   */
  public GenericResponseBuilder<T> type(MediaType type) {
    rawBuilder.type(type);
    return this;
  }

  /**
   * Sets the response entity media type.
   *
   * @param type the response entity media type, {@code null} will clear the media type
   * @return this builder
   */
  public GenericResponseBuilder<T> type(String type) {
    rawBuilder.type(type);
    return this;
  }

  /**
   * Sets the response variant data.
   *
   * <p>The variant data includes the content type, language, and encoding.
   *
   * @param variant the variant data for the response, {@code null} will clear the variant (and
   *     associated values)
   * @return this builder
   * @see #type(MediaType)
   * @see #language(Locale)
   * @see #encoding(String)
   * @see Variant
   */
  public GenericResponseBuilder<T> variant(Variant variant) {
    rawBuilder.variant(variant);
    return this;
  }

  /**
   * Sets the content location.
   *
   * @param location the content location, {@code null} will clear the content location
   * @return this builder
   */
  public GenericResponseBuilder<T> contentLocation(URI location) {
    rawBuilder.contentLocation(location);
    return this;
  }

  /**
   * Sets the response cookies.
   *
   * @param cookies the cookies for the response, {@code null} will clear the response cookies
   * @return this builder
   */
  public GenericResponseBuilder<T> cookie(NewCookie... cookies) {
    rawBuilder.cookie(cookies);
    return this;
  }

  /**
   * Sets the expiration date for the response.
   *
   * @param expires the response expiration date, {@code null} clears the expires date
   * @return this builder
   */
  public GenericResponseBuilder<T> expires(Date expires) {
    rawBuilder.expires(expires);
    return this;
  }

  /**
   * Sets the response entity last modified date.
   *
   * @param lastModified the date the response entity was last modified, {@code null} will clear the
   *     last modified date
   * @return this builder
   */
  public GenericResponseBuilder<T> lastModified(Date lastModified) {
    rawBuilder.lastModified(lastModified);
    return this;
  }

  /**
   * Sets the response location.
   *
   * @param location the response location, relative URIs will be converted to absolute URIs,
   *     {@code null} will clear the location
   * @return this builder
   */
  public GenericResponseBuilder<T> location(URI location) {
    rawBuilder.location(location);
    return this;
  }

  /**
   * Sets the response entity tag.
   *
   * @param tag the response entity tag, {@code null} will clear the entity tag
   * @return this builder
   */
  public GenericResponseBuilder<T> tag(EntityTag tag) {
    rawBuilder.tag(tag);
    return this;
  }

  /**
   * Sets the response entity tag.
   *
   * @param tag the response entity tag, {@code null} will clear the entity tag
   * @return this builder
   */
  public GenericResponseBuilder<T> tag(String tag) {
    rawBuilder.tag(tag);
    return this;
  }

  /**
   * Sets the variant list for the {@code Vary} header.
   *
   * @param variants the list of variants for the {@code Vary} header, {@code null} will clear the
   *     variants.
   * @return this builder
   */
  public GenericResponseBuilder<T> variants(Variant... variants) {
    rawBuilder.variants(variants);
    return this;
  }

  /**
   * Sets the variant list for the {@code Vary} header.
   *
   * @param variants the list of variants for the {@code Vary} header, {@code null} will clear the
   *     variants.
   * @return this builder
   */
  public GenericResponseBuilder<T> variants(List<Variant> variants) {
    rawBuilder.variants(variants);
    return this;
  }

  /**
   * Sets the link headers.
   *
   * @param links the links to be added to the response headers, {@code null} will clear the link
   *     headers
   * @return this builder
   */
  public GenericResponseBuilder<T> links(Link... links) {
    rawBuilder.links(links);
    return this;
  }

  /**
   * Adds a link header.
   *
   * @param uri the URI of the link header
   * @param rel the value of the {@code rel} parameter
   * @return this builder
   */
  public GenericResponseBuilder<T> link(URI uri, String rel) {
    rawBuilder.link(uri, rel);
    return this;
  }

  /**
   * Adds a link header.
   *
   * @param uri the URI of the link header
   * @param rel the value of the {@code rel} parameter
   * @return this builder
   */
  public GenericResponseBuilder<T> link(String uri, String rel) {
    rawBuilder.link(uri, rel);
    return this;
  }
}
