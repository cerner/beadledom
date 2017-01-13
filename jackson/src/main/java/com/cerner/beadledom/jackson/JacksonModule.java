package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;

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
 * <p>Installs:
 * <ul>
 *   <li> {@link MultibindingsScanner} </li>
 * </ul>
 *
 * @author John Leacox
 */
public class JacksonModule extends AbstractModule {

  @Override
  protected void configure() {

    // Empty multibindings for dependencies
    Multibinder<Module> jacksonModuleBinder = Multibinder.newSetBinder(binder(), Module.class);

    Multibinder<SerializationFeatureFlag> serializationFeatureBinder = Multibinder
        .newSetBinder(binder(), SerializationFeatureFlag.class);
    Multibinder<DeserializationFeatureFlag> deserializationFeatureBinder = Multibinder
        .newSetBinder(binder(), DeserializationFeatureFlag.class);
    Multibinder<JsonGeneratorFeatureFlag> jsonGeneratorFeatureBinder = Multibinder
        .newSetBinder(binder(), JsonGeneratorFeatureFlag.class);
    Multibinder<JsonParserFeatureFlag> jsonParserFeatureBinder = Multibinder
        .newSetBinder(binder(), JsonParserFeatureFlag.class);
    Multibinder<MapperFeatureFlag> mapperFeatureBinder = Multibinder
        .newSetBinder(binder(), MapperFeatureFlag.class);

    /**
     * MultibindingsScanner will scan all modules for methods with the annotations @ProvidesIntoMap,
     * @ProvidesIntoSet, and @ProvidesIntoOptional.
     */
    install(MultibindingsScanner.asModule());

    bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
  }
}
