package com.cerner.beadledom.client;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Guice binding annotation for the {@link CorrelationIdFilter} header name.
 *
 * @author John Leacox
 * @since 1.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface CorrelationIdClientHeader {
}
