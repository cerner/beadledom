package com.cerner.beadledom.client.jaxrs

import javax.ws.rs.core.Configuration

import com.cerner.beadledom.client.BeadledomClientBuilder
import org.mockito.Mockito
import org.scalatest.funspec.AnyFunSpec

/**
 * @author John Leacox
 */
class BeadledomClientBuilderSpec extends AnyFunSpec {
  describe("BeadledomClientBuilder") {
    describe("#create") {
      it("throws an IllegalStateException if an implementation is not found through SPI") {
        intercept[IllegalStateException] {
          BeadledomClientBuilder.newBuilder()
        }
      }
    }

    describe("#newClient") {
      it("throws an IllegalStateException if an implementation is not found through SPI") {
        intercept[IllegalStateException] {
          BeadledomClientBuilder.newClient()
        }
      }
    }

    describe("#newClient(Configuration)") {
      it("throws an IllegalStateException if an implementation is not found through SPI") {
        intercept[IllegalStateException] {
          val config: Configuration = Mockito.mock(classOf[Configuration])
          BeadledomClientBuilder.newClient(config)
        }
      }
    }
  }
}
