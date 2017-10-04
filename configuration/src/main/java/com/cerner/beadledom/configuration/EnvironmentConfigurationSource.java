package com.cerner.beadledom.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.EnvironmentConfiguration;

/**
 * An implementation class of {@link ConfigurationSource} for the source of environment variables.
 *
 * @see <a href="https://docs.oracle.com/javase/tutorial/essential/environment/env.html">Environment Variables</a>
 *
 * @author Nathan Schile
 * @since 2.7
 */
public class EnvironmentConfigurationSource extends AbstractConfigurationSource {

  public static final int DEFAULT_PRIORITY = 400;
  private final int priority;
  private final Configuration configuration;

  private EnvironmentConfigurationSource(EnvironmentConfiguration configuration, int priority) {
    if (configuration == null) {
      throw new NullPointerException("configuration: null");
    }
    if (priority < 0) {
      throw new IllegalArgumentException("priority of a configuration cannot be negative");
    }

    this.configuration = configuration;
    this.priority = priority;
  }

  /**
   * Creates an instance of {@link EnvironmentConfigurationSource}.
   */
  public static EnvironmentConfigurationSource create() {
    return EnvironmentConfigurationSource.create(DEFAULT_PRIORITY);
  }

  /**
   * Creates an instance of {@link EnvironmentConfigurationSource}.
   *
   * @param priority priority at which this {@link ConfigurationSource} to be loaded among other
   *     Configuration sources
   */
  public static EnvironmentConfigurationSource create(int priority) {
    return new EnvironmentConfigurationSource(new EnvironmentConfiguration(), priority);
  }

  /**
   * Creates an instance of {@link EnvironmentConfigurationSource}.
   *
   * @param configuration the system configuration to delegate to
   * @param priority priority at which this {@link ConfigurationSource} to be loaded among other
   *     Configuration sources
   */
  public static EnvironmentConfigurationSource create(
      EnvironmentConfiguration configuration, int priority) {
    return new EnvironmentConfigurationSource(configuration, priority);
  }

  @Override
  public Configuration getConfig() {
    return configuration;
  }

  @Override
  public int getPriority() {
    return priority;
  }
}
