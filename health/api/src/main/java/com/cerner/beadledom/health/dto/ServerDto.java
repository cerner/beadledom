package com.cerner.beadledom.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.time.Instant;
import java.util.Optional;

/**
 * Represents the runtime and environment information of the server for a health check response.
 *
 * <p>This can be used to construct health dependency response.
 *
 * @since 1.4
 */
@ApiModel(description = "Runtime and environment information of the server.")
@AutoValue
public abstract class ServerDto {
  /**
   * Creates a new builder for {@code ServerDto}.
   */
  public static Builder builder() {
    return new AutoValue_ServerDto.Builder()
        .setHostName(Optional.empty())
        .setStartupDateTime(Optional.empty());
  }

  @ApiModelProperty("The name of the host which served this health check response")
  @JsonProperty("hostName")
  public abstract Optional<String> getHostName();

  @ApiModelProperty("The startup date/time of the service process that served the health check "
      + "response in ISO-8601 format")
  @JsonProperty("startupDateTime")
  public abstract Optional<Instant> getStartupDateTime();

  @AutoValue.Builder
  public abstract static class Builder {
    abstract Builder setHostName(Optional<String> hostName);

    public Builder setHostName(String hostName) {
      return setHostName(Optional.ofNullable(hostName));
    }

    abstract Builder setStartupDateTime(Optional<Instant> startupDateTime);

    public Builder setStartupDateTime(Instant startupDateTime) {
      return setStartupDateTime(Optional.ofNullable(startupDateTime));
    }

    public abstract ServerDto build();
  }
}
