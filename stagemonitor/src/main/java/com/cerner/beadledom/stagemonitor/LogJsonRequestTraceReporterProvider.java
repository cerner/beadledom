package com.cerner.beadledom.stagemonitor;

import com.cerner.beadledom.stagemonitor.request.LogJsonRequestTraceReporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * An injection provider for {@link LogJsonRequestTraceReporter}.
 *
 * @author John Leacox
 * @since 1.2
 */
class LogJsonRequestTraceReporterProvider implements Provider<LogJsonRequestTraceReporter> {
  private final ObjectMapper objectMapper;

  @Inject
  LogJsonRequestTraceReporterProvider(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public LogJsonRequestTraceReporter get() {
    return new LogJsonRequestTraceReporter(objectMapper);
  }
}
