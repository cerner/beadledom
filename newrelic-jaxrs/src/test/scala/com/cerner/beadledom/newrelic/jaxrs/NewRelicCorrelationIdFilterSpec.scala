package com.cerner.beadledom.newrelic.jaxrs

import com.cerner.beadledom.newrelic.NewRelicApi
import javax.ws.rs.container.ContainerRequestContext
import org.mockito.Mockito.{reset, verify, verifyZeroInteractions, when}
import org.scalatest._
import org.scalatest.mock.MockitoSugar

class NewRelicCorrelationIdFilterSpec
  extends FunSpec with BeforeAndAfter with MustMatchers with MockitoSugar {

  val headerName = "Correlation-Id"
  val id = java.util.UUID.randomUUID.toString
  val request: ContainerRequestContext = mock[ContainerRequestContext]
  val newRelic = mock[NewRelicApi]
  var filter: NewRelicCorrelationIdFilter = _

  before {
    reset(newRelic, request)
  }

  describe("constructor") {
    it("throws a NullPointerException when the header name is null") {
      intercept[NullPointerException] {
        new NewRelicCorrelationIdFilter(null, newRelic)
      }
    }
    it("throws a NullPointerException when the new relic api is null") {
      intercept[NullPointerException] {
        new NewRelicCorrelationIdFilter("correlation-id", null)
      }
    }
    it("constructs a NewRelicCorrelationIdFilter") {
      new NewRelicCorrelationIdFilter(headerName, newRelic)
    }
  }

  describe("filter") {
    describe("when correlation id header is present") {
      it("adds the custom parameter on New Relic") {
        when(request.getHeaderString(headerName)).thenReturn(id, Nil: _*)
        filter = new NewRelicCorrelationIdFilter(headerName, newRelic)
        filter.filter(request)
        verify(newRelic).addCustomParameter(headerName, id)
      }
    }

    describe("when correlation id request property is present") {
      it("adds the custom parameter on New Relic") {
        when(request.getProperty(headerName)).thenReturn(id, Nil: _*)
        filter = new NewRelicCorrelationIdFilter(headerName, newRelic)
        filter.filter(request)
        verify(newRelic).addCustomParameter(headerName, id)
      }
    }

    describe("when correlation id request property is not present and header is not present") {
      it("does not add the custom parameter on New Relic") {
        filter = new NewRelicCorrelationIdFilter(headerName, newRelic)
        filter.filter(request)
        verifyZeroInteractions(newRelic)
      }
    }
  }
}
