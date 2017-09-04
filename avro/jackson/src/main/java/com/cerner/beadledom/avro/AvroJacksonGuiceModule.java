package com.cerner.beadledom.avro;

import com.fasterxml.jackson.databind.Module;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;

/**
 * A Guice module that provides Jackson Avro serialization support for Jackson.
 *
 * <p>Provides:
 * <ul>
 *     <li>Avro serialization support for Jackson via {@link Multibinder Multibinder&lt;Module&gt;}</li>
 * </ul>
 *
 * <p>Requires:
 * <ul>
 *     <li>{@link com.cerner.beadledom.metadata.BuildInfo}</li>
 * </ul>
 *
 * <p>Installs:
 * <ul>
 *   <li> {@link MultibindingsScanner} </li>
 * </ul>
 *
 * @author John Leacox
 */
public class AvroJacksonGuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<Module> jacksonModuleBinder = Multibinder.newSetBinder(binder(), Module.class);

    jacksonModuleBinder.addBinding().to(AvroJacksonModule.class);

    install(MultibindingsScanner.asModule());
  }
}
