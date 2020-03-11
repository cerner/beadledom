package com.cerner.beadledom.client;

import com.cerner.beadledom.guice.BindingAnnotations;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.OptionalBinder;

import java.lang.annotation.Annotation;

/**
 * The core guice module for Beadledom client.
 *
 * <p>To use this module install it using the {@link BeadledomClientModule#with(Class)} method
 * providing your own client binding annotation. This binding annotation is needed to namespace
 * different clients used by a single consumer. With the provided implementation of
 * {@link BeadledomClient}, JaxRs resource classes can be proxied by your own Guice module and
 * provided to consumers of your client.
 *
 * <p>Provides:
 * <ul>
 *     <li>
 *       Correlation id header name (String annotated with {@link CorrelationIdClientHeader} with a
 *       default value of "Correlation-Id". The default value can be overridden using the
 *       {@link OptionalBinder}.
 *     </li>
 *     <li>
 *       The optional binder for {@link BeadledomClientConfiguration}.
 *     </li>
 * </ul>
 *
 * <p>Installs:
 * <ul>
 *   <li> {@link BeadledomClientPrivateModule} </li>
 * </ul>
 *
 * <p>Exposes the following via the {@link BeadledomClientPrivateModule}:
 * <ul>
 *    <li> {@link BeadledomClientBuilder} </li>
 *    <li> {@link BeadledomClient} </li>
 *    <li> {@link BeadledomClientLifecycleHook} </li>
 * </ul>
 *
 * @author John Leacox
 * @author Sundeep Paruvu
 * @since 2.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
public class BeadledomClientModule extends AbstractModule {
  private final Class<? extends Annotation> clientBindingAnnotation;

  private BeadledomClientModule(Class<? extends Annotation> clientBindingAnnotation) {
    this.clientBindingAnnotation = clientBindingAnnotation;
  }

  /**
   * Creates a new instance of {@code BeadledomClientModule} for the specified binding
   * annotation.
   */
  public static BeadledomClientModule with(Class<? extends Annotation> clientBindingAnnotation) {
    BindingAnnotations.checkIsBindingAnnotation(clientBindingAnnotation);
    return new BeadledomClientModule(clientBindingAnnotation);
  }

  @Override
  protected void configure() {
    OptionalBinder
        .newOptionalBinder(
            binder(),
            Key.get(String.class, CorrelationIdClientHeader.class))
        .setDefault().toInstance(CorrelationIdContext.DEFAULT_HEADER_NAME);

    OptionalBinder
        .newOptionalBinder(
            binder(),
            Key.get(BeadledomClientConfiguration.class, clientBindingAnnotation));

    install(new BeadledomClientPrivateModule(clientBindingAnnotation));
  }
}
