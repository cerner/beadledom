package com.cerner.beadledom.client.resteasy

import org.scalatest.{FunSpec, MustMatchers}

/**
 * Spec for ExponentialBackoffHttpRequestRetryHandler
 *
 * @author John Leacox
 */
class ExponentialBackoffHttpRequestRetryHandlerSpec extends FunSpec with MustMatchers {
  describe("ExponentialBackoffHttpRequestRetryHandler") {
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
