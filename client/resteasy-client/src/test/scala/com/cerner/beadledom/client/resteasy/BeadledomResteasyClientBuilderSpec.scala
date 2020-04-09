package com.cerner.beadledom.client.resteasy

import com.cerner.beadledom.client.BeadledomClientBuilder
import org.scalatest.BeforeAndAfter

import scala.language.postfixOps
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
  * @author John Leacox
  */
class BeadledomResteasyClientBuilderSpec extends AnyFunSpec with Matchers with BeforeAndAfter {
  describe("BeadledomResteasyClientBuilder") {
    describe("ServiceLoader") {
      it("loads the resteasy builder implementation") {
        val builder = BeadledomResteasyClientBuilder.newBuilder()

        builder shouldBe a[BeadledomResteasyClientBuilder]
      }
    }

    describe("Client Configuration") {

      it("Default values") {
        val builder = BeadledomResteasyClientBuilder.newBuilder()
            .getBeadledomClientConfiguration

        builder.connectionPoolSize() should be(200)
        builder.maxPooledPerRouteSize() should be(100)
        builder.socketTimeoutMillis() should be(10000)
        builder.connectionTimeoutMillis() should be(10000)
        builder.ttlMillis() should be(1800000)
      }
    }
  }
}
