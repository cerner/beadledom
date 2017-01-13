package com.cerner.beadledom.stagemonitor;

import com.cerner.beadledom.stagemonitor.request.LogJsonRequestTraceReporter;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.stagemonitor.requestmonitor.RequestMonitor;

/**
 * A class for managing the lifecycle of the Stagemonitor RequestMonitor.
 *
 * @author John Leacox
 * @since 1.2
 */
class RequestMonitorLifecycle {
  private final LogJsonRequestTraceReporter jsonReporter;

  @Inject
  RequestMonitorLifecycle(LogJsonRequestTraceReporter jsonReporter) {
    this.jsonReporter = jsonReporter;
  }

  @PostConstruct
  void initializeRequestTraceReporters() {
    RequestMonitor.addRequestTraceReporter(jsonReporter);
  }
}
