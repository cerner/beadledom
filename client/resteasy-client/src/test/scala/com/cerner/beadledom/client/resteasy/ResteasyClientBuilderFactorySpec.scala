package com.cerner.beadledom.client.resteasy

import org.scalatest.{FunSpec, MustMatchers}

/**
  * Specs for ResteasyClientBuilderFactory.
  */
class ResteasyClientBuilderFactorySpec extends FunSpec with MustMatchers {

  val factory = new ResteasyClientBuilderFactory()

  describe("ResteasyClientBuilderFactory") {
    describe("#create") {
      it("creates a new builder") {
        val builder = factory.create()
        builder mustBe an[BeadledomResteasyClientBuilder]
      }
    }
  }
}

