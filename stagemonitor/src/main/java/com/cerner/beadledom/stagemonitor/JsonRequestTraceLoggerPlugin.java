package com.cerner.beadledom.stagemonitor;

import com.cerner.beadledom.stagemonitor.request.LogJsonRequestTraceReporter;
import org.stagemonitor.core.StagemonitorPlugin;
import org.stagemonitor.core.configuration.ConfigurationOption;
import org.stagemonitor.requestmonitor.RequestMonitorPlugin;

/**
 * A plugin providing configuration for the {@link LogJsonRequestTraceReporter}.
 *
 * @author Nathan Schile
 * @since 1.6
 */
public class JsonRequestTraceLoggerPlugin extends StagemonitorPlugin {

  private final ConfigurationOption<Long> minJsonExecutionTimeMillis =
      ConfigurationOption.longOption()
          .key("stagemonitor.requestmonitor.minJsonExecTimeMillis")
          .dynamic(false)
          .label("Request min execution time (millis)")
          .description("Only log callstacks for requests that execute longer than this value.")
          .defaultValue(1000L)
          .configurationCategory(RequestMonitorPlugin.REQUEST_MONITOR_PLUGIN)
          .build();

  public long getMinJsonExecutionTimeMillis() {
    return minJsonExecutionTimeMillis.getValue();
  }
}
