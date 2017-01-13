package com.cerner.beadledom.jaxrs;

import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * A JAX-RS based response with additional generic information about the response entity body.
 *
 * <p>This simplifies the consumption of a response with an entity body by tracking the type
 * information of the response entity and allowing direct access to the body via the
 * {@link #body()} method.
 *
 * <p>To get the most value out of this class it should be used as part of a JAX-RS Resource
 * interface definition that is shared by both a server implementation and a client auto-generated
 * proxy. Some value can still be had by using this for client auto-generated proxies only.
 *
 * <p>Note: this is an interface, rather than an abstract class like {@link Response}, so that
 * implementations can implement both {@link Response} and {@link GenericResponse}.
 *
 * @author John Leacox
 * @since 1.3
 * @see Response
 */
public interface GenericResponse<T> {
  /**
   * Returns true if {@link #getStatus()} is in the range [200..300), false otherwise.
   */
  boolean isSuccessful();

  /**
   * Get the response entity body, {@code null} if no entity body is present.
   */
  T body();

  /**
   * Get the error body, {@code null} if the response is not an error or no entity body is present.
   */
  ErrorBody errorBody();

  /**
   * Get the status of the response response.
   */
  int getStatus();

  /**
   * Get the status of the response response.
   */
  Response.StatusType getStatusInfo();

  /**
   * Closes the underlying response input stream.
   *
   * @throws ProcessingException if an error occurs while closing the response
   * @see Response#close()
   */
  void close();

  /**
   * Gets the response entity media type, or {@code null} of there is no response entity.
   */
  MediaType getMediaType();

  /**
   * Gets the language of the response entity, {@code null} if not specified.
   */
  Locale getLanguage();

  /**
   * Gets the {@code Content-Length} of the response if present, {@code -1} otherwise.
   */
  int getLength();

  /**
   * Gets the allowed HTTP methods.
   */
  Set<String> getAllowedMethods();

  /**
   * Gets any new cookies on the response.
   */
  Map<String, NewCookie> getCookies();

  /**
   * Gets the entity tag.
   */
  EntityTag getEntityTag();

  /**
   * Gets the resposne date, {@code null} if not present.
   */
  Date getDate();

  /**
   * Gets the last modified date, {@code null} if not present.
   */
  Date getLastModified();

  /**
   * Gets the location, {@code null} if not present.
   */
  URI getLocation();

  /**
   * Gets the links from the response headers.
   *
   * @see Response#getLinks()
   */
  Set<Link> getLinks();

  /**
   * Returns true if a link exists for the specified relation, false otherwise.
   */
  boolean hasLink(String relation);

  /**
   * Gets the link for the relation, {@code null} if not present.
   *
   * @see Response#getLink(String)
   */
  Link getLink(String relation);

  /**
   * Returns a link builder for the relation, {@code null} if not present.
   * @see Response#getLinkBuilder(String)
   */
  Link.Builder getLinkBuilder(String relation);

  /**
   * Gets a view of the response headers.
   *
   * @see Response#getHeaders()
   * @see #getStringHeaders()
   * @see #getHeaderString
   */
  MultivaluedMap<String, Object> getHeaders();

  /**
   * Gets a view of the response headers.
   *
   * @see Response#getStringHeaders()
   * @see #getHeaders()
   * @see #getHeaderString
   */
  MultivaluedMap<String, String> getStringHeaders();

  /**
   * Gets a response header as a string value.
   *
   * <p>If the header contains multiple values, they will be joined by a {@code ','} character,
   * {@code null} if the header is not present.
   *
   * @see Response#getHeaderString(String)
   * @see #getHeaders()
   * @see #getHeaderString
   */
  String getHeaderString(String name);
}
