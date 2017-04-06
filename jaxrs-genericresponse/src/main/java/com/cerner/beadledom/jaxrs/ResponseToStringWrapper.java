package com.cerner.beadledom.jaxrs;

import javax.ws.rs.core.Response;

/**
 * Simple class that wraps {@link Response} and implements {@link #toString()} for {@link Response}.
 */
public class ResponseToStringWrapper {

  private final Response response;

  /**
   * Constructs the wrapper.
   *
   * @param response
   *    the response to wrap
   */
  public ResponseToStringWrapper(Response response) {
    if (response == null) {
      throw new NullPointerException("response:null");
    }
    this.response = response;
  }

  @Override
  public String toString() {
    return "Response{status=" + response.getStatus() + ", mediaType="
      + response.getMediaType() + ", date=" + response.getDate() + ", length=" + response.getLength()
      + ", lastModified=" + response.getLastModified() + ", entityTag=" + response.getEntityTag()
      + ", language=" + response.getLanguage() + ", location=" + response.getLocation()
      + ", headers=" + response.getHeaders() + ", cookies=" + response.getCookies() + ", links="
      + response.getLinks() + " }";
  }

}
