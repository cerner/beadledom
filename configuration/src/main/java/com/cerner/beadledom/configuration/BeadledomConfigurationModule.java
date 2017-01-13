package com.cerner.beadledom.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Singleton;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Guice Module to create injections for {@link Configuration}.
 *
 * <p>Provides:
 * <ul>
 *   <li>
 *     an {@link ImmutableHierarchicalConfiguration} created from multiple
 *     {@link ConfigurationSource}s.
 *   </li>
 * </ul>
 *
 * <p><strong>Note:</strong> The order in which the configurations are loaded depends on the natural
 * ordering of the {@link ConfigurationSource}s. When two configuration sources has the same natural
 * ordering it can cause unwanted results. So it is the consumers responsibility to make sure that
 * every {@link ConfigurationSource} gets a unique natural ordering via priority.
 *
 * @author Sundeep Paruvu
 * @see ConfigurationSourcesModuleBuilder
 * @see ConfigurationSource
 * @since 1.7
 */
public class BeadledomConfigurationModule extends AbstractModule {
  private static final Logger logger = LoggerFactory.getLogger(BeadledomConfigurationModule.class);

  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), ConfigurationSource.class);
    install(MultibindingsScanner.asModule());
  }

  @Provides
  @Singleton
  ImmutableHierarchicalConfiguration providesConfiguration(
      Set<ConfigurationSource> configurationSources) {
    CombinedConfiguration config = new CombinedConfiguration();

    List<ConfigurationSource> sortedConfigurations = new ArrayList<ConfigurationSource>();
    sortedConfigurations.addAll(configurationSources);
    Collections.sort(sortedConfigurations);

    ConfigurationSource previousConfigSource = null;
    for (ConfigurationSource configSource : sortedConfigurations) {
      if (previousConfigSource != null) {
        warnIfEqualPriority(previousConfigSource, configSource);
      }
      config.addConfiguration(configSource.getConfig());
      previousConfigSource = configSource;
    }
    config.setNodeCombiner(new OverrideCombiner());
    return config;
  }

  private void warnIfEqualPriority(
      ConfigurationSource configSource1, ConfigurationSource configSource2) {
    if (configSource1.getPriority() == configSource2.getPriority()) {
      logger.warn("Configuration Sources {} and {} has the same priority {}. This could "
              + "cause unexpected results when accessing a property that exists in both "
              + "the configuration sources.", configSource1, configSource2,
          configSource2.getPriority());
    }
  }
}
