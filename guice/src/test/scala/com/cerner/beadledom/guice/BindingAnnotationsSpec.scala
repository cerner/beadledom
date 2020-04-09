package com.cerner.beadledom.guice

import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[BindingAnnotations]].
  *
  * @author John Leacox
  */
class BindingAnnotationsSpec extends AnyFunSpec with Matchers with BeforeAndAfter {
  describe("BindingAnnotations") {
    describe("#isBindingAnnotation") {
      it("returns false for a non-binding annotation") {
        BindingAnnotations.isBindingAnnotation(classOf[Override]) mustBe false
      }

      it("returns true for a BindingAnnotation") {
        BindingAnnotations.isBindingAnnotation(classOf[TestBindingAnnotation]) mustBe true
      }

      it("returns true for a Qualifier") {
        BindingAnnotations.isBindingAnnotation(classOf[TestQualifier]) mustBe true
      }
    }

    describe("#checkIsBindingAnnotation") {
      it("throws an IllegalArgumentException for a non-binding annotation") {
        intercept[IllegalArgumentException] {
          BindingAnnotations.checkIsBindingAnnotation(classOf[Override])
        }
      }

      it("does not throw an exception and returns the annotation for a BindingAnnotation") {
        BindingAnnotations.checkIsBindingAnnotation(classOf[TestBindingAnnotation]) mustBe
            classOf[TestBindingAnnotation]
      }

      it("does not throw an exception and returns the annotation for a Qualifier") {
        BindingAnnotations.checkIsBindingAnnotation(classOf[TestQualifier]) mustBe
            classOf[TestQualifier]
      }
    }
  }
}
