package com.cerner.beadledom.integration.client;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A default client feature annotation.
 *
 * @author Nick Behrens
 */
@BindingAnnotation
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface IntegrationClientFeature {
}
