package com.cerner.beadledom.metadata;

import com.google.auto.value.AutoValue;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides metadata about a running instance of a service.
 */
@AutoValue
public abstract class ServiceMetadata {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetadata.class);

  /**
   * Creates a new builder for {@code ServiceMetadata}.
   */
  public static Builder builder() {
    return new AutoValue_ServiceMetadata.Builder().setHostName(Optional.empty());
  }

  /**
   * Creates a new builder initialized with the values from the provided serviceMetadata.
   */
  public static Builder builder(ServiceMetadata serviceMetadata) {
    return new AutoValue_ServiceMetadata.Builder(serviceMetadata);
  }

  /**
   * Creates an instance of {@code ServiceMetadata} with the given build info, using the current
   * time for startup time and determining the host name automatically.
   */
  public static ServiceMetadata create(BuildInfo buildInfo) {
    String hostName = null;
    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      LOGGER.warn("Unable to determine host name", e);
    }
    return builder()
        .setBuildInfo(buildInfo)
        .setHostName(hostName)
        .setStartupTime(Instant.now())
        .build();
  }

  /**
   * Returns the build details of the assembly.
   */
  public abstract BuildInfo getBuildInfo();

  /**
   * Returns the host that the service is running on, e.g. "node001".
   *
   * <p>The returned host may be an IP address if hostname could not be determined, or
   * {@link Optional#empty()} if the IP address could not be determined.
   */
  public abstract Optional<String> getHostName();

  /**
   * Returns approximately when the service was started.
   */
  public abstract Instant getStartupTime();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setBuildInfo(BuildInfo buildInfo);

    public abstract Builder setHostName(Optional<String> hostName);

    public Builder setHostName(String hostName) {
      return setHostName(Optional.ofNullable(hostName));
    }

    public abstract Builder setStartupTime(Instant startupTime);

    public abstract ServiceMetadata build();
  }
}
