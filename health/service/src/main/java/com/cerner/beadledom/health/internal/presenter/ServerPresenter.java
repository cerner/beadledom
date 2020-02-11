package com.cerner.beadledom.health.internal.presenter;

import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.health.dto.ServerDto;
import java.time.Instant;
import java.util.Optional;

/**
 * Wraps a ServerDto to add methods needed by the HTML views.
 *
 * @author Nimesh Subramanian
 * @since 1.4
 */
public class ServerPresenter {
  private final ServerDto serverInfo;

  /**
   * Creates an instance of {@code ServerPresenter}.
   */
  public ServerPresenter(ServerDto dto) {
    this.serverInfo = checkNotNull(dto, "dto: null");
  }

  /**
   * Returns the host name from the DTO.
   */
  public Optional<String> getHostName() {
    return serverInfo.getHostName();
  }

  /**
   * Returns the startUpDateTime from the DTO.
   */
  public Optional<Instant> getStartupDateTime() {
    return serverInfo.getStartupDateTime();
  }
}
