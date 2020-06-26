package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * A Guice module that provides Jackson JSON serialization using {@link ObjectMapper}.
 *
 * <p>With the module installed, you can add new {@link Module Jackson modules} to the
 * {@link ObjectMapper} by adding bindings of {@link Module} using the Multibinder.
 * Example:
 * <pre>
 *     {@code
 *     class MyModule extends AbstractModule {
 *       {@literal @}Override
 *       protected void configure() {
 *         Multibinder&lt;Module&gt; jacksonModuleBinder = Multibinder
 *             .newSetBinder(binder(), Module.class);
 *         jacksonModuleBinder.addBinding().to(FoobarModule.class);
 *       }
 *     }}
 * </pre>
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link ObjectMapper}</li>
 * </ul>
 *
 * @author John Leacox
 */
public class JacksonModule extends AbstractModule {

  @Override
  protected void configure() {

    // Empty multibindings for dependencies
    Multibinder.newSetBinder(binder(), Module.class);

    Multibinder.newSetBinder(binder(), SerializationFeatureFlag.class);
    Multibinder.newSetBinder(binder(), DeserializationFeatureFlag.class);
    Multibinder.newSetBinder(binder(), JsonGeneratorFeatureFlag.class);
    Multibinder.newSetBinder(binder(), JsonParserFeatureFlag.class);
    Multibinder.newSetBinder(binder(), MapperFeatureFlag.class);

    bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
  }
}
