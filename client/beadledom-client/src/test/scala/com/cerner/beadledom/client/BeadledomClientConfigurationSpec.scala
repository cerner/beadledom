package com.cerner.beadledom.client

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class BeadledomClientConfigurationSpec extends FunSpec with Matchers with BeforeAndAfter{

  describe("BeadledomClientConfiguration") {

    it("returns default values") {
      val clientConfig : BeadledomClientConfiguration = BeadledomClientConfiguration.builder().build()
      clientConfig.connectionPoolSize() should be(200)
      clientConfig.maxPooledPerRouteSize() should be(100)
      clientConfig.socketTimeoutMillis() should be(10000)
      clientConfig.connectionTimeoutMillis() should be(10000)
      clientConfig.ttlMillis() should be(1800000)
    }
  }
}
