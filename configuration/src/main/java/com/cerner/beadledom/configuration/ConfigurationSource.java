package com.cerner.beadledom.configuration;

import org.apache.commons.configuration2.Configuration;

/**
 * Definition of the source based on a specific format of the {@link Configuration} .
 *
 * <p>It is strictly recommended that no Concrete class should implement this interface directly.
 * Instead they should be extend from {@link AbstractConfigurationSource} to ensure that all the
 * ConfigurationSources are compared using the same method
 * {@link AbstractConfigurationSource#compareTo(ConfigurationSource)}.
 *
 * <p>Every implementation of {@link ConfigurationSource} must have a priority (an {@link Integer}
 * value) set on them. Configurations are loaded based on the priority set on them. The properties
 * from the lower priority configurations are overriden by the higher priority configurations.
 *
 * <p><strong>Note</strong>: The implementations of this interface have a natural ordering that is
 * inconsistent with the {@link Object#equals} method. Two Configuration classes might have an equal
 * priority but might not be equal. Hence the natural ordering on the implementations of this
 * interface MUST NOT be used to determine the equality of the objects. The Natural ordering of the
 * instances can be obtained from the {@link ConfigurationSource#getPriority} method.
 *
 * @author Sundeep Paruvu
 * @see java.lang.Comparable
 * @see AbstractConfigurationSource
 * @since 1.7
 */
public interface ConfigurationSource extends Comparable<ConfigurationSource> {

  /**
   * Returns the Configuration from the ConfigurationSource.
   *
   * @return a {@link Configuration} object.
   */
  Configuration getConfig();

  /**
   * Returns the priority of the current ConfigurationSources among other ConfigurationSources.
   *
   * <p>The return value signifies the natural ordering of the instances. Greater Value implies
   * higher priority. Higher Priority ConfigurationSources overrides any matching properties from
   * the lower priority ConfigurationSources.
   *
   * <p>The priority on a {@link ConfigurationSource} <strong>must</strong> not be negative.
   *
   * @return an integer indicating the priority of the configuration.
   */
  int getPriority();
}
