package com.cerner.beadledom.stagemonitor

import com.cerner.beadledom.stagemonitor.request.LogJsonRequestTraceReporter
import com.google.inject.Guice
import org.scalatest.{FunSpec, MustMatchers}

/**
  * Unit tests for [[StagemonitorModule]].
  *
  * @author John Leacox
  */
class StagemonitorModuleSpec extends FunSpec with MustMatchers {
  describe("StagemonitorModule") {
    it("binds RequestMonitorLifecycle") {
      val injector = Guice.createInjector(new StagemonitorModule)
      injector.getInstance(classOf[RequestMonitorLifecycle]) mustBe an[RequestMonitorLifecycle]
    }

    it("binds LogJsonRequestTraceReporter") {
      val injector = Guice.createInjector(new StagemonitorModule)
      injector.getInstance(classOf[LogJsonRequestTraceReporter]) mustBe
          an[LogJsonRequestTraceReporter]
    }
  }
}
