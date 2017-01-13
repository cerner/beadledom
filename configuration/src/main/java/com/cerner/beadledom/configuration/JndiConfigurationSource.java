package com.cerner.beadledom.configuration;

import javax.naming.Context;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.JNDIConfiguration;

/**
 * An implementation class for {@link ConfigurationSource} for the source of type {@link Context}.
 *
 * <p>A {@link JNDIConfiguration} instance is created from the context. The context further be
 * filtered to a specific prefix.
 *
 * @author Sundeep Paruvu
 * @since 1.7
 */
public final class JndiConfigurationSource extends AbstractConfigurationSource {
  public static final int DEFAULT_PRIORITY = 300;
  private final int priority;
  private final Configuration configuration;

  private JndiConfigurationSource(Configuration configuration, int priority) {
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
   * Creates an instance of {@link JndiConfigurationSource}.
   *
   * @param context Context required to build the {@link JNDIConfiguration}
   * @param prefix prefix at which the context is further filtered to load the properties
   * @param priority priority at which this {@link ConfigurationSource} to be loaded among other
   *     Configuration sources
   * @throws NullPointerException when context is null
   * @throws IllegalArgumentException when the priority is a negative value
   */
  public static JndiConfigurationSource create(Context context, String prefix, int priority) {
    return new JndiConfigurationSource(createJndiConfiguration(context, prefix), priority);
  }

  /**
   * Creates an instance of {@link JndiConfigurationSource}.
   *
   * @param context Context required to build the {@link JNDIConfiguration}
   * @param priority priority at which this {@link ConfigurationSource} to be loaded among other
   *     Configuration sources
   * @throws NullPointerException when context is null
   * @throws IllegalArgumentException when the priority is a negative value
   */
  public static JndiConfigurationSource create(Context context, int priority) {
    return new JndiConfigurationSource(createJndiConfiguration(context, null), priority);
  }

  /**
   * Creates an instance of {@link JndiConfigurationSource}.
   *
   * @param context Context required to build the {@link JNDIConfiguration}
   * @param prefix prefix at which the context is further filtered to load the properties
   * @throws NullPointerException when context is null
   */
  public static JndiConfigurationSource create(Context context, String prefix) {
    return new JndiConfigurationSource(createJndiConfiguration(context, prefix), DEFAULT_PRIORITY);
  }

  /**
   * Creates an instance of {@link JndiConfigurationSource}.
   *
   * @param context Context required to build the {@link JNDIConfiguration}
   * @throws NullPointerException when context is null
   */
  public static JndiConfigurationSource create(Context context) {
    if (context == null) {
      throw new NullPointerException("context: null");
    }
    return new JndiConfigurationSource(createJndiConfiguration(context, null), DEFAULT_PRIORITY);
  }

  private static Configuration createJndiConfiguration(Context context, String prefix) {
    if (context == null) {
      throw new NullPointerException("context: null");
    }
    return new JNDIConfiguration(context, prefix);
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
