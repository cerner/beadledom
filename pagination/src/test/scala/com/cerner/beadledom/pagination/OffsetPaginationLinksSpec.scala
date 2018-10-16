package com.cerner.beadledom.pagination

import com.google.inject.Guice
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.UriInfo
import org.jboss.resteasy.spi.ResteasyUriInfo
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}
import scala.collection.JavaConverters._

/**
 * Spec for OffsetPaginationLinks.
 *
 * @author John Leacox
 * @author Ian Kottman
 */
class OffsetPaginationLinksSpec extends FunSpec with MustMatchers with MockitoSugar {

  // Allow static injection for pagination parameters
  Guice.createInjector(new OffsetPaginationModule())
  private val list = OffsetPaginatedList.builder()
      .items(List("a", "b", "c").asJava)
      .metadata("limit", null, "offset", null, null, true)
      .build()

  describe("OffsetPaginationLinks") {
    describe("#create") {
      it("throws a WebApplicationException with 400 status when offset is not a number") {
        val uriInfo = mockUriInfo(queryParams = ("offset", "blue"), ("limit", "2"))

        val exception = intercept[WebApplicationException] {
          OffsetPaginationLinks.create(list, uriInfo)
        }
        exception.getResponse.getStatus mustBe 400
      }

      it("throws a WebApplicationException with 400 status when offset is negative") {
        val uriInfo = mockUriInfo(queryParams = ("offset", "-1"))

        val exception = intercept[WebApplicationException] {
          OffsetPaginationLinks.create(list, uriInfo)
        }
        exception.getResponse.getStatus mustBe 400
      }

      it("throws a WebApplicationException with 400 status when limit is not a number") {
        val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "purple"))

        val exception = intercept[WebApplicationException] {
          OffsetPaginationLinks.create(list, uriInfo)
        }
        exception.getResponse.getStatus mustBe 400
      }

      it("throws a WebApplicationException with 400 status when limit is negative") {
        val uriInfo = mockUriInfo(queryParams = ("limit", "-1"))

        val exception = intercept[WebApplicationException] {
          OffsetPaginationLinks.create(list, uriInfo)
        }
        exception.getResponse.getStatus mustBe 400
      }

      it("throws a WebApplicationException with 400 status when limit is greater than 100") {
        val uriInfo = mockUriInfo(queryParams = ("limit", "101"))

        val exception = intercept[WebApplicationException] {
          OffsetPaginationLinks.create(list, uriInfo)
        }
        exception.getResponse.getStatus mustBe 400
      }

      it("uses 0 as the default offset if not set") {
        val uriInfo = mockUriInfo(queryParams = ("offset", null), ("limit", "2"))
        val links = OffsetPaginationLinks.create(list, uriInfo)
        links.firstLink() must include("offset=0")
      }

      it("it uses the default value for limit if not set") {
        val uriInfo = mockUriInfo(queryParams = ("offset", "2"), ("limit", null))
        val limitedList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c").asJava)
            .metadata("limit", 7, "offset", 0L, 3L, true)
            .build()
        val links = OffsetPaginationLinks.create(limitedList, uriInfo)
        links.firstLink() must include("limit=7")
      }
    }

    describe("with total results") {
      describe("with total results less than page limit") {
        // allow static injection of the pagination parameters
        Guice.createInjector(new OffsetPaginationModule())

        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d").asJava)
            .metadata("limit", null, "offset", null, 3L, null)
            .build()
        val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "4"))
        val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

        describe("when on first page") {
          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=4"
          }

          it("has a last link") {
            links.lastLink() mustBe "example.com?offset=0&limit=4"
          }

          it("does not have a next link") {
            links.nextLink() mustBe null
          }

          it("does not have a previous link") {
            links.prevLink() mustBe null
          }
        }
      }

      describe("with total results more than page limit") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d", "e", "f", "g", "h").asJava)
            .metadata("limit", null, "offset", null, 500L, null)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() mustBe "example.com?offset=480&limit=20"
          }

          it("has a next link") {
            links.nextLink() mustBe "example.com?offset=20&limit=20"
          }

          it("does not have a previous link") {
            links.prevLink() mustBe null
          }
        }

        describe("when on last page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "480"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() mustBe "example.com?offset=480&limit=20"
          }

          it("does not have a next link") {
            links.nextLink() mustBe null
          }

          it("has a previous link") {
            links.prevLink() mustBe "example.com?offset=460&limit=20"
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() mustBe "example.com?offset=480&limit=20"
          }

          it("has a next link") {
            links.nextLink() mustBe "example.com?offset=120&limit=20"
          }

          it("has a previous link") {
            links.prevLink() mustBe "example.com?offset=80&limit=20"
          }
        }
      }

      describe("with total results not a multiple of limit") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d", "e", "f", "g", "h").asJava)
            .metadata("limit", null, "offset", null, 510L, null)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() mustBe "example.com?offset=500&limit=20"
          }

          it("has a next link") {
            links.nextLink() mustBe "example.com?offset=20&limit=20"
          }

          it("does not have a previous link") {
            links.prevLink() mustBe null
          }
        }

        describe("when on last page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "500"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() mustBe "example.com?offset=500&limit=20"
          }

          it("does not have a next link") {
            links.nextLink() mustBe null
          }

          it("has a previous link") {
            links.prevLink() mustBe "example.com?offset=480&limit=20"
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() mustBe "example.com?offset=500&limit=20"
          }

          it("has a next link") {
            links.nextLink() mustBe "example.com?offset=120&limit=20"
          }

          it("has a previous link") {
            links.prevLink() mustBe "example.com?offset=80&limit=20"
          }
        }
      }
    }

    describe("without total results") {
      describe("with has more true") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d").asJava)
            .metadata("limit", null, "offset", null, null, true)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() mustBe null
          }

          it("has a next link") {
            links.nextLink() mustBe "example.com?offset=20&limit=20"
          }

          it("does not have a previous link") {
            links.prevLink() mustBe null
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() mustBe null
          }

          it("has a next link") {
            links.nextLink() mustBe "example.com?offset=120&limit=20"
          }

          it("has a previous link") {
            links.prevLink() mustBe "example.com?offset=80&limit=20"
          }
        }
      }

      describe("with has more false") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d").asJava)
            .metadata("limit", null, "offset", null, null, false)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() mustBe null
          }

          it("does not have a next link") {
            links.nextLink() mustBe null
          }

          it("does not have a previous link") {
            links.prevLink() mustBe null
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks.create(totalResultsList, uriInfo)

          it("has a first link") {
            links.firstLink() mustBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() mustBe null
          }

          it("does not have a next link") {
            links.nextLink() mustBe null
          }

          it("has a previous link") {
            links.prevLink() mustBe "example.com?offset=80&limit=20"
          }
        }
      }
    }

    describe("when totalResults and hasMore contradict") {
      val list = OffsetPaginatedList.builder()
          .items(List("a", "b", "c", "d").asJava)
          .metadata("limit", 20, "offset", 0L, 100L, false)
          .build()
      val uriInfo = mockUriInfo()
      val links = OffsetPaginationLinks.create(list, uriInfo)

      it("has a first link") {
        links.firstLink() mustBe "example.com?offset=0&limit=20"
      }

      it("has a last link") {
        links.lastLink() mustBe "example.com?offset=80&limit=20"
      }

      it("has a next link") {
        links.nextLink() mustBe "example.com?offset=20&limit=20"
      }

      it("does not have a previous link") {
        links.prevLink() mustBe null
      }
    }
  }

  private def mockUriInfo(queryParams: (String, String)*): UriInfo = {
    val queryString = queryParams
        .filter({ case (_, v) => v != null })
        .map({ case (k, v) => s"$k=$v" }).mkString("&")
    val uriInfo = new ResteasyUriInfo("example.com", queryString, "")

    uriInfo
  }
}
