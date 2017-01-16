package com.cerner.beadledom.resteasy

import com.cerner.beadledom.jaxrs.GenericResponseBuilderFactory
import com.cerner.beadledom.testing.UnitSpec
import java.lang.annotation.Annotation

/**
  * Spec tests for [[ResteasyGenericResponseBuilderFactory]]
  *
  * @author Nimesh Subramanian
  * @author John Leacox
  */
class ResteasyGenericResponseBuilderFactorySpec extends UnitSpec {
  describe("ResteasyGenericResponseBuilderFactory") {
    describe("#newInstance") {
      it("loads the resteasy builder factory implementation") {
        GenericResponseBuilderFactory.newInstance() mustBe a[ResteasyGenericResponseBuilderFactory]
      }
    }

    describe("#create") {
      it("creates a new builder with the given status code") {
        val response = new ResteasyGenericResponseBuilderFactory().create(200).build()
        response.getStatus mustBe 200
      }

      it("creates a new builder with the given status code and body") {
        val response = new ResteasyGenericResponseBuilderFactory().create(200, "Hello World")
            .build()
        response.getStatus mustBe 200
        response.body() mustBe "Hello World"
      }

      it("creates a new builder with the given status code, body, and annotations") {
        val response = new ResteasyGenericResponseBuilderFactory()
            .create(200, "Hello World", Array[Annotation]()).build()
        response.getStatus mustBe 200
        response.body() mustBe "Hello World"
      }
    }
  }
}
