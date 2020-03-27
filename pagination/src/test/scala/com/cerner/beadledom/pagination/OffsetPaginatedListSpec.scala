package com.cerner.beadledom.pagination

import org.scalatest.{FunSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar

/**
 * Spec for OffsetPaginationList.
 *
 * @author John Leacox
 */
class OffsetPaginatedListSpec extends FunSpec with MustMatchers with MockitoSugar {
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
