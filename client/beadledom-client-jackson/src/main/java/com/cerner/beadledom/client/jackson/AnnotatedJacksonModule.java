package com.cerner.beadledom.client.jackson;

import com.cerner.beadledom.guice.BindingAnnotations;
import com.cerner.beadledom.jackson.DeserializationFeatureFlag;
import com.cerner.beadledom.jackson.JacksonModule;
import com.cerner.beadledom.jackson.JsonGeneratorFeatureFlag;
import com.cerner.beadledom.jackson.JsonParserFeatureFlag;
import com.cerner.beadledom.jackson.MapperFeatureFlag;
import com.cerner.beadledom.jackson.SerializationFeatureFlag;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;
import java.lang.annotation.Annotation;

/**
 * A Guice module that provides Jackson JSON serialization using {@link ObjectMapper}.
 *
 * <p>This module provides the {@link ObjectMapper} in the context of the given
 * {@link BindingAnnotation}.
 *
 * <p>With this module installed, you can add new {@link Module Jackson modules} to the
 * {@link ObjectMapper} by adding bindings of {@link Module} using the Multibinder.
 * Example:
 * <pre>{@code
 *     class MyModule extends AbstractModule {
 *       {@literal @}Override
 *       protected void configure() {
 *         Multibinder&lt;Module&gt; jacksonModuleBinder = Multibinder
 *             .newSetBinder(binder(), Module.class, MyBindingAnnotation.class);
 *         jacksonModuleBinder.addBinding().to(FoobarModule.class);
 *       }
 *     }}
 * </pre>
 *
 * <p>Binds:
 * <ul>
 *   <li>
 *     {@link ObjectMapper} annotated with the given
 *     {@link AnnotatedJacksonModule#clientBindingAnnotation}
 *   </li>
 *   <li>
 *     {@link JacksonJsonProvider} annotated with the given
 *     {@link AnnotatedJacksonModule#clientBindingAnnotation} via a guice provider
 *     {@link AnnotatedObjectMapperProvider}
 *   </li>
 *   <li>
 *     A {@link Multibinder} for {@link SerializationFeatureFlag} annotated with
 *     {@link AnnotatedJacksonModule#clientBindingAnnotation}
 *   </li>
 *   <li>
 *     A {@link Multibinder} for {@link DeserializationFeatureFlag} annotated with
 *     {@link AnnotatedJacksonModule#clientBindingAnnotation}
 *   </li>
 *   <li>
 *     A {@link Multibinder} for {@link JsonGeneratorFeatureFlag} annotated with
 *     {@link AnnotatedJacksonModule#clientBindingAnnotation}
 *   </li>
 *   <li>
 *     A {@link Multibinder} for {@link JsonParserFeatureFlag} annotated with
 *     {@link AnnotatedJacksonModule#clientBindingAnnotation}
 *   </li>
 *   <li>
 *     A {@link Multibinder} for {@link MapperFeatureFlag} annotated with
 *     {@link AnnotatedJacksonModule#clientBindingAnnotation}
 *   </li>
 * </ul>
 *
 * <p>Installs:
 * <ul>
 *   <li> {@link MultibindingsScanner} </li>
 * </ul>
 *
 * <p>This module also creates a new SetBinders for the following types annotated with the given
 * {@link BindingAnnotation}
 * <ul>
 *   <li>{@link Module}</li>
 *   <li>{@link SerializationFeatureFlag}</li>
 *   <li>{@link DeserializationFeatureFlag}</li>
 *   <li>{@link JsonGeneratorFeatureFlag}</li>
 *   <li>{@link JsonParserFeatureFlag}</li>
 *   <li>{@link MapperFeatureFlag}</li>
 * </ul>
 *
 * @author Sundeep Paruvu
 * @since 2.2
 * @see JacksonModule
 */
public class AnnotatedJacksonModule extends AbstractModule {

  private final Class<? extends Annotation> clientBindingAnnotation;

  private AnnotatedJacksonModule(Class<? extends Annotation> clientBindingAnnotation) {
    this.clientBindingAnnotation = clientBindingAnnotation;
  }

  /**
   * A static method to create an instance of {@link AnnotatedJacksonModule}.
   *
   * @param clientBindingAnnotation a {@link BindingAnnotation} to which the {@link ObjectMapper}
   *     need to be annotated with.
   *
   * @return an instance of {@link AnnotatedJacksonModule}
   */
  public static AnnotatedJacksonModule with(Class<? extends Annotation> clientBindingAnnotation) {
    if (clientBindingAnnotation == null) {
      throw new NullPointerException("clientBindingAnnotation:null");
    }

    BindingAnnotations.checkIsBindingAnnotation(clientBindingAnnotation);

    return new AnnotatedJacksonModule(clientBindingAnnotation);
  }

  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), Module.class, clientBindingAnnotation);
    Multibinder.newSetBinder(binder(), SerializationFeatureFlag.class, clientBindingAnnotation);
    Multibinder.newSetBinder(binder(), DeserializationFeatureFlag.class, clientBindingAnnotation);
    Multibinder.newSetBinder(binder(), JsonGeneratorFeatureFlag.class, clientBindingAnnotation);
    Multibinder.newSetBinder(binder(), JsonParserFeatureFlag.class, clientBindingAnnotation);
    Multibinder.newSetBinder(binder(), MapperFeatureFlag.class, clientBindingAnnotation);

    /**
     * MultibindingsScanner will scan all modules for methods with the annotations @ProvidesIntoMap,
     * @ProvidesIntoSet, and @ProvidesIntoOptional.
     */
    install(MultibindingsScanner.asModule());
    install(AnnotatedJacksonPrivateModule.with(clientBindingAnnotation));
  }
}
