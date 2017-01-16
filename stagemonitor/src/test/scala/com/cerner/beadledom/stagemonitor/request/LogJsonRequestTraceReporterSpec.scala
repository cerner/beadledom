package com.cerner.beadledom.stagemonitor.request

import com.cerner.beadledom.stagemonitor.JsonRequestTraceLoggerPlugin
import com.fasterxml.jackson.databind.ObjectMapper
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.slf4j.Logger
import org.stagemonitor.requestmonitor.reporter.RequestTraceReporter.{IsActiveArguments, ReportArguments}
import org.stagemonitor.requestmonitor.{RequestMonitorPlugin, RequestTrace}

/**
  * Spec tests for [[LogJsonRequestTraceReporter]].
  *
  * @author John Leacox
  */
class LogJsonRequestTraceReporterSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("LogJsonRequestTraceReporter") {

    val jsonRequestLoggerPlugin = mock[JsonRequestTraceLoggerPlugin]
    when(jsonRequestLoggerPlugin.getMinJsonExecutionTimeMillis).thenReturn(1000L)

    describe("#reportRequestTrace") {
      val requestMonitorPlugin = mock[RequestMonitorPlugin]

      it("does not log if info logging is disabled") {
        val logger = mock[Logger]
        when(logger.isInfoEnabled).thenReturn(false)

        val reporter = new LogJsonRequestTraceReporter(logger, new ObjectMapper(),
          requestMonitorPlugin, jsonRequestLoggerPlugin)

        val trace = mock[RequestTrace]
        when(trace.getExecutionTime).thenReturn(2000)

        val args = new ReportArguments(trace)

        reporter.reportRequestTrace(args)

        verify(logger, never()).info(any())
      }

      it("does not log if the execution time is faster than the specified minimum") {
        val logger = mock[Logger]

        when(logger.isInfoEnabled).thenReturn(true)
        val reporter = new LogJsonRequestTraceReporter(logger, new ObjectMapper(),
          requestMonitorPlugin, jsonRequestLoggerPlugin)

        val trace = mock[RequestTrace]
        when(trace.getExecutionTime).thenReturn(500)

        val args = new ReportArguments(trace)

        reporter.reportRequestTrace(args)

        verify(logger, never()).info(any())
      }

      it("logs the request trace as info") {
        val logger = mock[Logger]
        when(logger.isInfoEnabled).thenReturn(true)

        val reporter = new LogJsonRequestTraceReporter(logger, new ObjectMapper(),
          requestMonitorPlugin, jsonRequestLoggerPlugin)

        val trace = mock[RequestTrace]
        when(trace.getExecutionTime).thenReturn(2000)

        val args = new ReportArguments(trace)

        reporter.reportRequestTrace(args)

        verify(logger).info(any())
      }
    }

    describe("#isActive") {
      it("is false if requestMonitorPlugin is not logging call stacks") {
        val logger = mock[Logger]
        when(logger.isInfoEnabled).thenReturn(true)

        val requestMonitorPlugin = mock[RequestMonitorPlugin]
        when(requestMonitorPlugin.isLogCallStacks).thenReturn(false)

        val reporter = new LogJsonRequestTraceReporter(logger, new ObjectMapper(),
          requestMonitorPlugin, jsonRequestLoggerPlugin)

        val trace = mock[RequestTrace]

        val args = new IsActiveArguments(trace)

        reporter.isActive(args) must be(false)
      }

      it("is true if requestMonitorPlugin is logging call stacks") {
        val logger = mock[Logger]
        when(logger.isInfoEnabled).thenReturn(true)

        val requestMonitorPlugin = mock[RequestMonitorPlugin]
        when(requestMonitorPlugin.isLogCallStacks).thenReturn(true)

        val reporter = new LogJsonRequestTraceReporter(logger, new ObjectMapper(),
          requestMonitorPlugin, jsonRequestLoggerPlugin)

        val trace = mock[RequestTrace]

        val args = new IsActiveArguments(trace)

        reporter.isActive(args) must be(true)
      }
    }
  }
}
