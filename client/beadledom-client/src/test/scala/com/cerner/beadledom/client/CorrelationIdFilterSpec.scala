package com.cerner.beadledom.client.jaxrs

import com.cerner.beadledom.client.CorrelationIdFilter
import org.scalatest._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
  * Spec for the {@link CorrelationIdFilter}
  *
  * @author John Leacox
  */
class CorrelationIdFilterSpec extends AnyFunSpec with BeforeAndAfter with Matchers
    with MockitoSugar with CorrelationIdFilterBehaviors {
  val defaultHeaderName = "Correlation-Id"

  val testCorrelationIdContext = new TestCorrelationIdContext

  after {
    testCorrelationIdContext.clear()
  }

  val defaultFilter = new CorrelationIdFilter(testCorrelationIdContext)
  val customFilter = new CorrelationIdFilter(testCorrelationIdContext, "customHeader")

  describe("CorrelationIdFilter") {
    describe("constructor") {
      it("throws a NullPointerException when the correlationIdContext is null") {
        intercept[NullPointerException] {
          new CorrelationIdFilter(null, defaultHeaderName)
        }
      }
    }

    describe("with default id names") {
      it should behave like
          correlationIdFilter(defaultFilter, testCorrelationIdContext, defaultHeaderName)
    }

    describe("with custom id names") {
      it should behave like
          correlationIdFilter(customFilter, testCorrelationIdContext, "customHeader")
    }
  }
}
