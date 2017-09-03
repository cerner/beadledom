package com.cerner.beadledom.client.resteasy

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

/**
 * Specs for ServiceUnavailablRetryInterceptor.
 *
 * @author John Leacox
 */
class ServiceUnavailableRetryInterceptorSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("ServiceUnavailablRetryInterceptor") {
    describe("#intercept") {
      it("does not retry a request that has a successful response") {
        // TODO
      }

      it("does not retry a request that has a client error response") {
        // TODO
      }

      it("does not retry a request that has a 500 response") {
        // TODO
      }

      it("returns the final failed response when all retries fail") {
        // TODO
      }

      it("retries a request that has a 503 response") {
        // TODO
      }
    }
  }
}
