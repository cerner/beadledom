package com.cerner.beadledom.resteasy;

import com.google.common.collect.Sets;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.plugins.interceptors.encoding.ServerContentEncodingAnnotationFilter;

/**
 * A Jax-Rs server filter for setting the gzip content-encoding header if the gzip accept-encoding
 * header is present.
 *
 * @author John Leacox
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class GzipContentEncodingFilter extends ServerContentEncodingAnnotationFilter {
  public GzipContentEncodingFilter() {
    super(Sets.newHashSet("gzip", "deflate"));
  }
}
