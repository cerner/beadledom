package com.cerner.beadledom.configuration;

import java.io.IOException;
import java.io.Reader;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

/**
 * An implementation class of {@link ConfigurationSource} for the source of type {@link XMLConfiguration}.
 *
 * <p>A property whose value is a sequence (ex: list, set) of objects must be placed in its own
 * attribute name. For example
 *
 * <pre>
 *   &lt;?xml version="1.0" encoding="UTF-8" ?&gt;
 *   &lt;config&gt;
 *     &lt;name&gt;Lays&lt;/name&gt;
 *     &lt;type&gt;Potato Chips&lt;/type&gt;
 *     &lt;flavors&gt;
 *         &lt;flavor&gt;Barbecue&lt;/flavor&gt;
 *         &lt;flavor&gt;Onion Cream&lt;/flavor&gt;
 *     &lt;/flavors&gt;
 *   &lt;/config&gt;
 * </pre>
 *
 * <p>The sequence values are accessed by fully qualifying the name of the attributes.
 *
 * <pre>
 *   config.getList("flavors.flavor");
 * </pre>
 *
 * @author Sundeep Paruvu
 * @since 1.7
 */
public final class XmlConfigurationSource extends AbstractConfigurationSource {
  public static final int DEFAULT_PRIORITY = 100;
  private final int priority;
  private final Configuration configuration;

  private XmlConfigurationSource(FileBasedConfiguration configuration, int priority) {
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
   * Creates an instance of {@link XmlConfigurationSource}.
   *
   * @throws ConfigurationException when fails to create the {@link XMLConfiguration}
   * @throws IOException when fails to read from the {@code reader}
   * @throws NullPointerException if the path to the xml file is null
   */
  public static XmlConfigurationSource create(Reader reader)
      throws ConfigurationException, IOException {
    return new XmlConfigurationSource(createConfiguration(reader), DEFAULT_PRIORITY);
  }

  /**
   * Creates an instance of {@link XmlConfigurationSource}.
   *
   * @throws ConfigurationException when fails to create the {@link XMLConfiguration}
   * @throws NullPointerException if the path to the xml file is null
   * @throws IllegalArgumentException if the path to the xml file is not valid or the
   *     priority is a  negative value
   */
  public static XmlConfigurationSource create(Reader reader, int priority)
      throws ConfigurationException {
    return new XmlConfigurationSource(createConfiguration(reader), priority);
  }

  private static FileBasedConfiguration createConfiguration(Reader reader)
      throws ConfigurationException {
    if (reader == null) {
      throw new NullPointerException("reader: null");
    }

    FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
        new FileBasedConfigurationBuilder<FileBasedConfiguration>(XMLConfiguration.class)
            .configure(new Parameters().xml());

    FileBasedConfiguration fileBasedConfiguration = builder.getConfiguration();

    FileHandler handler = new FileHandler(fileBasedConfiguration);
    handler.load(reader);
    return fileBasedConfiguration;
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
