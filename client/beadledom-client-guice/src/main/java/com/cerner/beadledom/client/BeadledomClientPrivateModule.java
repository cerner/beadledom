package com.cerner.beadledom.client;

import com.cerner.beadledom.client.resteasy.ResteasyClientBuilderFactory;
import com.cerner.beadledom.guice.dynamicbindings.AnnotatedModule;

import com.google.common.base.Optional;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.OptionalBinder;

import java.lang.annotation.Annotation;

/**
 * The Private guice module for Beadledom client that is intended to be use within
 * {@link BeadledomClientModule}.
 *
 * <p>It binds the following list of classes to the given {@code getBindingAnnotation()}.
 * <ul>
 *   <li>{@link BeadledomClientBuilder}</li>
 *   <li>{@link BeadledomClient}</li>
 *   <li>{@link BeadledomClientConfiguration}</li>
 *   <li>{@link BeadledomClientBuilderProvider}</li>
 *   <li>{@link BeadledomClientProvider}</li>
 *   <li>{@link BeadledomClientLifecycleHookProvider}</li>
 * </ul>
 *
 * <p>Configures:
 * <ul>
 *   <li>
 *      <ul> The following Optional binders:
 *        <li>
 *          {@link ResteasyClientBuilderFactory} as the default implementation for the
 *          {@link BeadledomClientBuilderFactory}. Optionally the consumer can provide their own
 *          implementation of the {@link BeadledomClientBuilderFactory}.
 *        </li>
 *      </ul>
 *   </li>
 *   <li>
 *      <ul> The following dynamic providers
 *        <li>{@link BeadledomClientBuilderFactory}</li>
 *        <li>{@link BeadledomClientBuilder}</li>
 *        <li>{@link BeadledomClient}</li>
 *        <li>Optional<{@link BeadledomClientConfiguration}></li>
 *      </ul>
 *    </li>
 * </ul>
 *
 * <p>Exposes:
 * <ul>
 *    <li> {@link BeadledomClientBuilder} </li>
 *    <li> {@link BeadledomClient} </li>
 *    <li> {@link BeadledomClientLifecycleHook} </li>
 * </ul>
 *
 * @author Sundeep Paruvu
 * @since 2.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
class BeadledomClientPrivateModule extends AnnotatedModule {

  BeadledomClientPrivateModule(Class<? extends Annotation> clientBindingAnnotation) {
    super(clientBindingAnnotation);
  }

  @Override
  protected void configure() {
    // Required to build BeadledomClientBuilder
    OptionalBinder
        .newOptionalBinder(
            binder(),
            Key.get(BeadledomClientBuilderFactory.class, getBindingAnnotation()))
        .setDefault().toInstance(new ResteasyClientBuilderFactory());


    bindDynamicProvider(BeadledomClientBuilderFactory.class);

    // Required to build BeadledomClientBuilder
    BeadledomClientBuilderProvider beadledomClientBuilderProvider =
        new BeadledomClientBuilderProvider(getBindingAnnotation());

    bind(BeadledomClientBuilder.class).annotatedWith(getBindingAnnotation())
        .toProvider(beadledomClientBuilderProvider)
        .asEagerSingleton();

    bindDynamicProvider(BeadledomClientBuilder.class);

    // Required to build BeadledomClientConfiguration
    TypeLiteral<Optional<BeadledomClientConfiguration>> clientConfigOptTypeLiteral =
        new TypeLiteral<Optional<BeadledomClientConfiguration>>() {
        };

    bindDynamicProvider(clientConfigOptTypeLiteral);

    //Required to build BeadledomClient
    BeadledomClientProvider beadledomClientProvider =
        new BeadledomClientProvider(getBindingAnnotation());

    bind(BeadledomClient.class).annotatedWith(getBindingAnnotation())
        .toProvider(beadledomClientProvider)
        .asEagerSingleton();

    bindDynamicProvider(BeadledomClient.class);

    // Lifecycle hook
    BeadledomClientLifecycleHookProvider provider =
        new BeadledomClientLifecycleHookProvider(getBindingAnnotation());

    bind(BeadledomClientLifecycleHook.class).annotatedWith(getBindingAnnotation())
        .toProvider(provider).asEagerSingleton();

    expose(BeadledomClientBuilder.class).annotatedWith(getBindingAnnotation());
    expose(BeadledomClient.class).annotatedWith(getBindingAnnotation());
    expose(BeadledomClientLifecycleHook.class).annotatedWith(getBindingAnnotation());
  }
}
