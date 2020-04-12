package com.cerner.beadledom.jaxrs.provider

import com.cerner.beadledom.correlation.ThreadLocalCorrelationContext
import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import org.slf4j.MDC

class CorrelationIdFilterSpec extends FunSpec with BeforeAndAfter with Matchers
with MockitoSugar with CorrelationIdFilterBehaviors {
  val defaultHeaderName = "Correlation-Id"
  val defaultMdcName = "Correlation-Id"
  val correlationContext = ThreadLocalCorrelationContext.create()
  after {
    MDC.clear()
  }

  val defaultFilter = new CorrelationIdFilter()
  val customFilter = new CorrelationIdFilter("customHeader", "customMdc", correlationContext)

  describe("CorrelationIdFilter") {
    describe("with default id names") {
      it should behave like correlationIdFilter(defaultFilter, defaultHeaderName, defaultMdcName, correlationContext)
    }

    describe("with custom id names") {
      it should behave like correlationIdFilter(customFilter, "customHeader", "customMdc", correlationContext)
    }
  }
}
