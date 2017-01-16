package com.cerner.beadledom.configuration;

import javax.annotation.Nonnull;
import org.apache.commons.configuration2.Configuration;

/**
 * A base class that defines the source of the {@link Configuration} based on a specific format of
 * the configuration.
 *
 * <p>This class is not intended to be instantiated directly hence the protected contructor.
 * Consumers should use the configuration format (ex: Java Properties, JNDI etc) specific extension
 * of {@link ConfigurationSource}.
 *
 * @author Sundeep Paruvu
 * @see ConfigurationSource
 * @since 2.1
 */
public abstract class AbstractConfigurationSource implements ConfigurationSource {
  /**
   * To achieve the natural ordering of {@link ConfigurationSource} i.e., higher priority sources
   * precedes lower priority sources the compareTo method is expected to reverse the ordering of the
   * natural numbers.
   */
  @Override
  public final int compareTo(@Nonnull ConfigurationSource that) {
    return that.getPriority() - this.getPriority();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConfigurationSource)) {
      return false;
    }

    ConfigurationSource that = (ConfigurationSource) o;

    return getPriority() == that.getPriority() && getConfig().equals(that.getConfig());
  }

  @Override
  public int hashCode() {
    int result = getPriority();
    result = 31 * result + getConfig().hashCode();
    return result;
  }
}
