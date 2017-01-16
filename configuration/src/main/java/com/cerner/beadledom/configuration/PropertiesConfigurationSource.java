package com.cerner.beadledom.configuration;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * An implementation class of {@link ConfigurationSource} for the source of type {@link Properties}.
 *
 * <p>A property whose value is a sequence (ex: list, set) of objects must be delimited by a
 * comma (<strong>,</strong>).
 *
 * @author Sundeep Paruvu
 * @since 1.7
 */
public final class PropertiesConfigurationSource extends AbstractConfigurationSource {
  public static final int DEFAULT_PRIORITY = 200;
  private final int priority;
  private final Configuration configuration;

  private PropertiesConfigurationSource(FileBasedConfiguration configuration, int priority) {
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
   * Creates an instance of {@link PropertiesConfigurationSource}.
   *
   * @throws ConfigurationException if failed to create the {@link PropertiesConfiguration}
   * @throws IOException if failed to read from {@code reader}.
   */
  public static PropertiesConfigurationSource create(Reader reader)
      throws ConfigurationException, IOException {
    return new PropertiesConfigurationSource(
        createPropertiesConfiguration(reader), DEFAULT_PRIORITY);
  }

  /**
   * Creates an instance of {@link PropertiesConfigurationSource}.
   *
   * @throws ConfigurationException if failed to create the {@link PropertiesConfiguration}
   * @throws IOException if failed to read from {@code reader}.
   */
  public static PropertiesConfigurationSource create(Reader reader, int priority)
      throws ConfigurationException, IOException {
    return new PropertiesConfigurationSource(createPropertiesConfiguration(reader), priority);
  }

  private static FileBasedConfiguration createPropertiesConfiguration(Reader reader)
      throws ConfigurationException, IOException {
    if (reader == null) {
      throw new NullPointerException("reader: null");
    }

    FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
        new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
            .configure(new Parameters()
                .properties()
                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));

    FileBasedConfiguration config = builder.getConfiguration();
    config.read(reader);
    return config;
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
