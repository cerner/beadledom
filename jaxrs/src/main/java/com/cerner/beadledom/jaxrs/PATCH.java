package com.cerner.beadledom.jaxrs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.HttpMethod;

/**
 * Indicates that the annotated method responds to HTTP PATCH requests.
 *
 * @deprecated Use PATCH annotation from JAX-RS 2.1, https://jax-rs.github.io/apidocs/2.1/javax/ws/rs/PATCH.html
 *
 * @author Eric Christensen
 * @since 2.5
 */
@Deprecated
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
public @interface PATCH {
}
