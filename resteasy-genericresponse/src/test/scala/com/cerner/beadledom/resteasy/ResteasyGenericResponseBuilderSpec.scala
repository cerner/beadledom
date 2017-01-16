package com.cerner.beadledom.resteasy

import com.cerner.beadledom.testing.UnitSpec
import java.lang.annotation.Annotation

/**
  * Specs for ResteasyGenericResponseBuilder
  *
  * @author Nimesh Subramanian
  */
class ResteasyGenericResponseBuilderSpec extends UnitSpec{
  describe("ResteasyGenericResponseBuilder") {
    describe("#create") {
      it("creates a new builder with the given status code") {
        val response = ResteasyGenericResponseBuilder.create(200).build()
        response.getStatus mustBe 200
      }

      it("creates a new builder with the given status code and body") {
        val response = ResteasyGenericResponseBuilder.create(200, "Hello World")
            .build()
        response.getStatus mustBe 200
        response.body() mustBe "Hello World"
      }

      it("creates a new builder with the given status code, body, and annotations") {
        val response = ResteasyGenericResponseBuilder
            .create(200, "Hello World", Array[Annotation]()).build()
        response.getStatus mustBe 200
        response.body() mustBe "Hello World"
      }
    }
  }
}
