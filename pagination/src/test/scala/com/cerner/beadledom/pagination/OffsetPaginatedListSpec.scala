package com.cerner.beadledom.pagination

import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec for OffsetPaginationList.
 *
 * @author John Leacox
 */
class OffsetPaginatedListSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("OffsetPaginatedList") {
    describe("Builder") {
      describe("#build") {
        it ("raises IllegalStateException when totalResults is negative") {
          val builder = OffsetPaginatedList.builder()
              .totalResults(-5L)

          val exception = intercept[IllegalStateException] {
            builder.build()
          }

          exception.getMessage mustBe "totalResults is negative"
        }
      }
    }
  }
}
