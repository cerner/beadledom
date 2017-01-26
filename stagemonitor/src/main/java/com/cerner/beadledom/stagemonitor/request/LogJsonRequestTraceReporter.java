package com.cerner.beadledom.stagemonitor.request;

import com.cerner.beadledom.stagemonitor.JsonRequestTraceLoggerPlugin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.requestmonitor.RequestMonitorPlugin;
import org.stagemonitor.requestmonitor.RequestTrace;
import org.stagemonitor.requestmonitor.profiler.CallStackElement;
import org.stagemonitor.requestmonitor.reporter.RequestTraceReporter;



/**
 * An implementation of {@link RequestTraceReporter} that logs the {@link RequestTrace} JSON
 * representation.
 *
 * @author Nathan Schile
 * @author John Leacox
 * @since 1.0
 */
public class LogJsonRequestTraceReporter extends RequestTraceReporter {
  private static final Logger errorLogger =
      LoggerFactory.getLogger(LogJsonRequestTraceReporter.class.getSimpleName() + ".Error");

  private final Logger logger;
  private final RequestMonitorPlugin requestMonitorPlugin;
  private final JsonRequestTraceLoggerPlugin jsonRequestTraceLoggerPlugin;
  private final ObjectMapper objectMapper;

  /**
   * Creates a new instance of {@link LogJsonRequestTraceReporter}.
   */
  public LogJsonRequestTraceReporter(ObjectMapper objectMapper) {
    this.logger = LoggerFactory.getLogger(LogJsonRequestTraceReporter.class);
    this.objectMapper = objectMapper;
    this.requestMonitorPlugin = Stagemonitor.getPlugin(RequestMonitorPlugin.class);
    this.jsonRequestTraceLoggerPlugin = Stagemonitor.getPlugin(JsonRequestTraceLoggerPlugin.class);
  }

  //@VisibleForTesting
  LogJsonRequestTraceReporter(
      Logger logger, ObjectMapper objectMapper, RequestMonitorPlugin requestMonitorPlugin,
      JsonRequestTraceLoggerPlugin jsonRequestTraceLoggerPlugin) {
    this.logger = logger;
    this.requestMonitorPlugin = requestMonitorPlugin;
    this.objectMapper = objectMapper;
    this.jsonRequestTraceLoggerPlugin = jsonRequestTraceLoggerPlugin;
  }

  @Override
  public void reportRequestTrace(ReportArguments reportArguments) throws Exception {
    RequestTrace requestTrace = reportArguments.getRequestTrace();

    if (logger.isInfoEnabled() && requestTrace.getExecutionTime() >= jsonRequestTraceLoggerPlugin
        .getMinJsonExecutionTimeMillis()) {
      try {
        CondensedCallStackElement condensedCallStackElement = getCondensedCallStack(requestTrace);
        logger.info(objectMapper
            .writeValueAsString(new RequestTraceWrapper(requestTrace, condensedCallStackElement)));
      } catch (JsonProcessingException ok) {
        // It's not valid JSON, this log should only contain JSON, so log to error logger instead
        errorLogger
            .error("Unable to serialize stack request trace to json. Request: {}", requestTrace);
      }
    }
  }

  private <T extends RequestTrace> CondensedCallStackElement getCondensedCallStack(T requestTrace) {
    CallStackElement callStack = requestTrace.getCallStack();
    if (callStack == null) {
      return null;
    }

    return CondensedCallStackElement.of(callStack);
  }

  @Override
  public boolean isActive(IsActiveArguments isActiveArguments) {
    return requestMonitorPlugin.isLogCallStacks();
  }
}

