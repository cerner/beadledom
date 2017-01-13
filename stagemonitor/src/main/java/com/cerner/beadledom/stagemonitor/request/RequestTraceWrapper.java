package com.cerner.beadledom.stagemonitor.request;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;
import org.stagemonitor.requestmonitor.RequestTrace;
import org.stagemonitor.web.monitor.HttpRequestTrace;

/**
 * Wrapper class for {@link org.stagemonitor.requestmonitor.RequestTrace}.
 *
 * <p>The wrapper formats the call stack by shortening the package name of call stack elements to
 * only include the first letter of the package names.
 *
 * @author Nathan Schile
 * @author John Leacox
 * @since 1.0
 */
@JsonPropertyOrder({"timestamp"})
class RequestTraceWrapper {

  private final RequestTrace requestTrace;
  private final boolean isHttpRequestTrace;
  private final CondensedCallStackElement callStack;

  RequestTraceWrapper(RequestTrace requestTrace, CondensedCallStackElement callStack) {
    this.requestTrace = requestTrace;
    this.isHttpRequestTrace = requestTrace instanceof HttpRequestTrace;
    this.callStack = callStack;
  }

  public String getApplication() {
    return requestTrace.getApplication();
  }

  public String getHost() {
    return requestTrace.getHost();
  }

  public String getInstance() {
    return requestTrace.getInstance();
  }

  public String getTimestamp() {
    return requestTrace.getTimestamp();
  }

  public String getClientIp() {
    return requestTrace.getClientIp();
  }

  public long getExecutionTime() {
    return requestTrace.getExecutionTime();
  }

  public long getExecutionTimeCpu() {
    return requestTrace.getExecutionTimeCpu();
  }

  public String getName() {
    return requestTrace.getName();
  }

  public Map<String, String> getParameter() {
    return requestTrace.getParameters();
  }

  /**
   * Returns a condensed call stack using {@link CondensedCallStackElement}.
   */
  public String getCallStack() {
    return callStack == null ? null : callStack.toString(true);
  }

  public String getUrl() {
    return isHttpRequestTrace ? ((HttpRequestTrace) requestTrace).getUrl() : null;
  }

  public Integer getStatusCode() {
    return isHttpRequestTrace ? ((HttpRequestTrace) requestTrace).getStatusCode() : null;
  }

  public String getMethod() {
    return isHttpRequestTrace ? ((HttpRequestTrace) requestTrace).getMethod() : null;
  }

  public Integer getBytesWritten() {
    return isHttpRequestTrace ? ((HttpRequestTrace) requestTrace).getBytesWritten() : null;
  }
}
