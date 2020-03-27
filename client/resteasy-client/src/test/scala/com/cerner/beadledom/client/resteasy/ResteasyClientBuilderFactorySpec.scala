package com.cerner.beadledom.client.resteasy

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Specs for ResteasyClientBuilderFactory.
  */
class ResteasyClientBuilderFactorySpec extends AnyFunSpec with Matchers {

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

