package com.cerner.beadledom.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import java.util.ArrayList;
import java.util.List;

/**
 * A Builder class that builds a Guice module for binding multiple {@link ConfigurationSource}s.
 *
 * <p>The Guice module that is built by this builder configures
 * <ul>
 *   <li> A {@link Multibinder} for {@link ConfigurationSource}</li>
 * </ul>
 *
 * <p>This builder can be used in multiple modules to bind multiple {@link ConfigurationSource}.
 * Consumer must use the {@link ConfigurationSourcesModuleBuilder#addSource(ConfigurationSource)}
 * builder method to build the list of configuration Sources.
 *
 * <p>The {@link ConfigurationSourcesModuleBuilder#build} method creates a new anonymous
 * {@link Module} that configures a {@link Multibinder} for the provided {@link ConfigurationSource}
 *
 * <p>Usage:
 * <pre><code>
 *   public class MyModule extends AbstractModule {
 *     {@literal @}Override
 *     protected void configure() {
 *        install(ConfigurationSourcesModuleBuilder.newBuilder()
 *          .addSource(source1)
 *          .addSource(source2)
 *          .addSource(source3)
 *          .build());
 *     }
 *   }
 * </code></pre>
 *
 * <p>It is the responsibility of the consumer to build the configuration sources with appropriate
 * priority (natural ordering). The priority or natural ordering of the {@link ConfigurationSource}s
 * is what is considered when loading the configurations from the corresponding sources instead of
 * the order in which they are configured using this Builder.
 *
 * @author Sundeep Paruvu
 * @see com.cerner.beadledom.configuration.BeadledomConfigurationModule
 * @since 2.1
 */
public final class ConfigurationSourcesModuleBuilder {

  private List<ConfigurationSource> configurationSources;

  private ConfigurationSourcesModuleBuilder() {
    this.configurationSources = new ArrayList<ConfigurationSource>();
  }

  /**
   * Static Convenience method create an instance of {@link ConfigurationSourcesModuleBuilder}.
   */
  public static ConfigurationSourcesModuleBuilder newBuilder() {
    return new ConfigurationSourcesModuleBuilder();
  }

  /**
   * Adds the given {@code configurationSource} to the list.
   *
   * @param configurationSource configuration source from which Configuration will be built.
   * @return the current instance of the {@link ConfigurationSourcesModuleBuilder}.
   */
  public ConfigurationSourcesModuleBuilder addSource(ConfigurationSource configurationSource) {
    if (configurationSource == null) {
      throw new NullPointerException("configurationSource: null");
    }
    configurationSources.add(configurationSource);
    return this;
  }

  /**
   * Builds the {@link ConfigurationSourcesModuleBuilder} with the list of
   * {@link ConfigurationSource}s built using the {@link ConfigurationSourcesModuleBuilder
   * #addSource(ConfigurationSource)} builder method.
   *
   * @return a Guice module that configures a Multibinder for ConfigurationSources
   */
  public Module build() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        Multibinder<ConfigurationSource> multiBinder =
            Multibinder.newSetBinder(binder(), ConfigurationSource.class);
        for (ConfigurationSource configurationSource : configurationSources) {
          multiBinder.addBinding().toInstance(configurationSource);
        }
      }
    };
  }
}
