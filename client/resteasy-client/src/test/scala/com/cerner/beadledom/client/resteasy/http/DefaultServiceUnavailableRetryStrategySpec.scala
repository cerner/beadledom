package com.cerner.beadledom.client.resteasy.http

import org.apache.http.client.ServiceUnavailableRetryStrategy
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.{HttpRequest, HttpResponse, RequestLine, StatusLine}
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

/**
 * @author John Leacox
 */
class DefaultServiceUnavailableRetryStrategySpec
    extends FunSpec with Matchers with MockitoSugar with RetryRequestBehaviors {
  describe("DefaultServiceUnavailableRetryStrategySpec") {
    describe("#getRetryInterval") {
      it("returns the retry interval given to the constructor") {
        val retryStrategy = new DefaultServiceUnavailableRetryStrategy(5)

        retryStrategy.getRetryInterval shouldBe 5
      }
    }

    val retryStrategy = new DefaultServiceUnavailableRetryStrategy(1000)

    describe("#retryRequest") {
      describe("with status code 500 and executionCount 0") {
        it should behave like nonRetryableRequest(retryStrategy, 500, 0)
      }

      describe("with status code 500 and executionCount 1") {
        it should behave like nonRetryableRequest(retryStrategy, 500, 1)
      }

      describe("with status code 500 and executionCount 2") {
        it should behave like nonRetryableRequest(retryStrategy, 500, 2)
      }

      describe("with status code 599 and executionCount 0") {
        it should behave like nonRetryableRequest(retryStrategy, 599, 0)
      }

      describe("with status code 503 and executionCount 0") {
        it should behave like retryableRequest(retryStrategy, 503, 0)
      }

      describe("with status code 503 and executionCount 1") {
        it should behave like retryableRequest(retryStrategy, 503, 1)
      }

      describe("with status code 503 and executionCount 2") {
        it should behave like retryableRequest(retryStrategy, 503, 2)
      }

      describe("with status code 503 and executionCount 3") {
        it should behave like nonRetryableRequest(retryStrategy, 503, 3)
      }

      describe("with status code 500 and executionCount 3") {
        it should behave like nonRetryableRequest(retryStrategy, 500, 3)
      }

      describe("with status code 499 and executionCount 0") {
        it should behave like nonRetryableRequest(retryStrategy, 499, 0)
      }

      describe("with status code 600 and executionCount 0") {
        it should behave like nonRetryableRequest(retryStrategy, 600, 0)
      }

      describe("with status code 499 and executionCount 3") {
        it should behave like nonRetryableRequest(retryStrategy, 499, 3)
      }
    }
  }
}

trait RetryRequestBehaviors extends Matchers with MockitoSugar {
  this: FunSpec =>

  def retryableRequest(retryStrategy: ServiceUnavailableRetryStrategy, statusCode: Int,
      executionCount: Int): Unit = {
    it(s"returns true") {
      val response = mockResponse(statusCode)
      val context = mockContext()

      retryStrategy.retryRequest(response, executionCount, context) shouldBe true
    }
  }

  def nonRetryableRequest(retryStrategy: ServiceUnavailableRetryStrategy, statusCode: Int,
      executionCount: Int): Unit = {
    it(s"returns false") {
      val response = mockResponse(statusCode)
      val context = mockContext()

      retryStrategy.retryRequest(response, executionCount, context) shouldBe false
    }
  }

  private def mockResponse(statusCode: Int): HttpResponse = {
    val statusLine = mock[StatusLine]
    Mockito.when(statusLine.getStatusCode).thenReturn(statusCode)

    val response = mock[HttpResponse]
    Mockito.when(response.getStatusLine).thenReturn(statusLine)

    response
  }

  private def mockContext(): HttpClientContext = {
    val requestLine = mock[RequestLine]
    Mockito.when(requestLine.getUri).thenReturn("some-uri")

    val request = mock[HttpRequest]
    Mockito.when(request.getRequestLine).thenReturn(requestLine)

    val context = mock[HttpClientContext]
    Mockito.when(context.getRequest).thenReturn(request)

    context
  }
}
