package com.cerner.beadledom.pagination.parameters

import com.cerner.beadledom.pagination.BeadledomPaginationModule

import com.google.inject.Guice

import javax.ws.rs.core.UriInfo
import org.jboss.resteasy.spi.ResteasyUriInfo
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

/**
  * Spec for OffsetPaginatedListLinksWriterInterceptor.
  *
  * @author Ian Kottman
  */
class PaginationParametersSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("PaginationParameters") {
    describe("#getLimit") {
      it("returns the default limit if no query parameter is present") {
        val injector = Guice.createInjector(new BeadledomPaginationModule())
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo()

        params.getLimit mustBe 20
      }

      it("returns the limit from the query parameter when present") {
        val injector = Guice.createInjector(new BeadledomPaginationModule())
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("limit", "10"))

        params.getLimit mustBe 10
      }

      it("returns the limit from a custom limit parameter") {
        val injector =
          Guice.createInjector(new BeadledomPaginationModule("test_limit", "test_offset"))
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("test_limit", "10"))

        params.getLimit mustBe 10
      }

      it("returns the custom default limit when no query parameter is present") {
        val injector =
          Guice.createInjector(new BeadledomPaginationModule(50, 0L))
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo()

        params.getLimit mustBe 50
      }
    }

    describe("#getOffset") {
      it("returns the default offset if it cannot find the limit parameter") {
        val injector = Guice.createInjector(new BeadledomPaginationModule())
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo()

        params.getOffset mustBe 0
      }

      it("returns the offset from the query parameter when present") {
        val injector = Guice.createInjector(new BeadledomPaginationModule())
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("offset", "30"))

        params.getOffset mustBe 30
      }

      it("returns the limit from a custom offset parameter") {
        val injector =
          Guice.createInjector(new BeadledomPaginationModule("test_limit", "test_offset"))
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("test_offset", "1"))

        params.getOffset mustBe 1L
      }

      it("returns the custom default offset when no query parameter is present") {
        val injector =
          Guice.createInjector(new BeadledomPaginationModule(50, 2L))
        val params = injector.getInstance(classOf[PaginationParameters])
        params.uriInfo = mockUriInfo()

        params.getOffset mustBe 2L
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
