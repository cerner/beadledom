package com.cerner.beadledom.pagination.parameters

import javax.ws.rs.core.UriInfo

import com.cerner.beadledom.pagination.OffsetPaginationModule
import com.cerner.beadledom.pagination.models.OffsetPaginationConfiguration
import com.google.inject.multibindings.OptionalBinder
import com.google.inject.{AbstractModule, Guice, Injector}
import org.jboss.resteasy.spi.ResteasyUriInfo
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

/**
  * Spec for OffsetPaginatedListLinksWriterInterceptor.
  *
  * @author Ian Kottman
  */
class OffsetPaginationParametersSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("OffsetPaginationParameters") {
    describe("#getLimit") {
      it("returns the default limit if no query parameter is present") {
        val injector = Guice.createInjector(new OffsetPaginationModule())
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
        params.uriInfo = mockUriInfo()

        params.getLimit mustBe 20
      }

      it("returns the limit from the query parameter when present") {
        val injector = Guice.createInjector(new OffsetPaginationModule())
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("limit", "10"))

        params.getLimit mustBe 10
      }

      it("returns the limit from a custom limit parameter") {
        val paginationConfig = OffsetPaginationConfiguration.builder()
          .setLimitFieldName("test_limit")
          .build()
        val injector = getInjector(paginationConfig)
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("test_limit", "10"))

        params.getLimit mustBe 10
      }

      it("returns the custom default limit when no query parameter is present") {
        val paginationConfig = OffsetPaginationConfiguration.builder().setDefaultLimit(50).build()
        val injector = getInjector(paginationConfig)
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
        params.uriInfo = mockUriInfo()

        params.getLimit mustBe 50
      }
    }

    describe("#getOffset") {
      it("returns the default offset if it cannot find the limit parameter") {
        val injector = Guice.createInjector(new OffsetPaginationModule())
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
        params.uriInfo = mockUriInfo()

        params.getOffset mustBe 0
      }

      it("returns the offset from the query parameter when present") {
        val injector = Guice.createInjector(new OffsetPaginationModule())
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("offset", "30"))

        params.getOffset mustBe 30
      }

      it("returns the limit from a custom offset parameter") {
        val paginationConfig = OffsetPaginationConfiguration.builder()
          .setOffsetFieldName("test_offset")
          .build()
        val injector = getInjector(paginationConfig)
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
        params.uriInfo = mockUriInfo(queryParams = ("test_offset", "1"))

        params.getOffset mustBe 1L
      }

      it("returns the custom default offset when no query parameter is present") {
        val paginationConfig = OffsetPaginationConfiguration.builder().setDefaultOffset(2L).build()
        val injector = getInjector(paginationConfig)
        val params = injector.getInstance(classOf[OffsetPaginationParameters])
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

  private def getInjector(paginationConfig: OffsetPaginationConfiguration): Injector = {
    val module = new AbstractModule() {
      override def configure(): Unit = {
        install(new OffsetPaginationModule)

        OptionalBinder.newOptionalBinder(binder, classOf[OffsetPaginationConfiguration])
          .setBinding()
          .toInstance(paginationConfig)
      }
    }

    Guice.createInjector(module)
  }
}
