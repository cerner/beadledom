package com.cerner.beadledom.client.resteasy

import java.net.SocketException

import javax.net.ssl.SSLException
import org.apache.http.{HttpRequest, HttpVersion}
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.message.BasicRequestLine
import org.mockito.Mockito
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

/**
 * Spec for ExponentialBackoffHttpRequestRetryHandler
 *
 * @author John Leacox
 */
class ExponentialBackoffHttpRequestRetryHandlerSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("ExponentialBackoffHttpRequestRetryHandler") {

    describe("#retryRequest") {
      val httpContext = mock[HttpClientContext]
      val request = mock[HttpRequest]
      val requestLine = new BasicRequestLine("GET", "http://www.google.com/", HttpVersion.HTTP_1_1)
      Mockito.when(request.getRequestLine).thenReturn(requestLine)
      Mockito.when(httpContext.getRequest).thenReturn(request)
      val retryHandler = new ExponentialBackoffHttpRequestRetryHandler(3, false)

      it ("retries on SSLException that are caused by SocketException") {
        val exception = new SSLException("SSLException", new SocketException())
        val retry = retryHandler.retryRequest(exception, 1, httpContext)
        retry mustBe true
      }

      it ("does not retry on SSLException that are caused by non-IOExceptions") {
        val exception = new SSLException("SSLException", new RuntimeException())
        val retry = retryHandler.retryRequest(exception, 1, httpContext)
        retry mustBe false
      }

      it ("does not retry on SSLException that have no exception cause") {
        val exception = new SSLException("SSLException")
        val retry = retryHandler.retryRequest(exception, 1, httpContext)
        retry mustBe false
      }
    }

    describe("#calculateRetryDelay") {
      it("returns 100 when executionCount is 1") {
        val retryHandler = new ExponentialBackoffHttpRequestRetryHandler(3, false)

        val retryDelay = retryHandler.calculateRetryDelay(1)

        retryDelay mustBe 100 +- 5
      }

      it("returns 200 when executionCount is 2") {
        val retryHandler = new ExponentialBackoffHttpRequestRetryHandler(3, false)

        val retryDelay = retryHandler.calculateRetryDelay(2)

        retryDelay mustBe 200 +- 5
      }

      it("returns 400 when executionCount is 3") {
        val retryHandler = new ExponentialBackoffHttpRequestRetryHandler(3, false)

        val retryDelay = retryHandler.calculateRetryDelay(3)

        retryDelay mustBe 400 +- 5
      }
    }
  }
}
