package com.cerner.beadledom.pagination

import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto
import com.google.inject.Guice
import javax.ws.rs.core.UriInfo
import org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext
import org.jboss.resteasy.spi.ResteasyUriInfo
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}
import scala.collection.JavaConverters._

/**
 * Spec for OffsetPaginatedListLinksWriterInterceptor.
 *
 * @author John Leacox
 */
class OffsetPaginatedListLinksWriterInterceptorSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("OffsetPaginatedListLinksWriterInterceptor") {
    describe("#aroundWriteTo") {
      it("replaces an OffsetPaginatedList entity with an OffsetPaginatedListDto entity") {
        // Allow static injection for pagination parameters
        Guice.createInjector(new OffsetPaginationModule())

        val list = OffsetPaginatedList.builder()
            .items(List("a", "b").asJava)
            .metadata("limit", 20, "offset", 0L, null, true)
            .build()
        val uriInfo = mockUriInfo()

        val interceptor = new OffsetPaginatedListLinksWriterInterceptor
        interceptor.uriInfo = uriInfo

        val context = mock[AbstractWriterInterceptorContext]
        when(context.setEntity(any())).thenCallRealMethod()
        when(context.getEntity).thenCallRealMethod()

        context.setEntity(list)
        interceptor.aroundWriteTo(context)
        val listWithLinks = context.getEntity.asInstanceOf[OffsetPaginatedListDto[String]]

        listWithLinks.items.asScala mustBe List("a", "b")
        listWithLinks.firstLink() mustBe "example.com?offset=0&limit=20"
        listWithLinks.lastLink() mustBe null
        listWithLinks.nextLink() mustBe "example.com?offset=20&limit=20"
        listWithLinks.prevLink() mustBe null
      }

      it("does not replace entities of other types") {
        val uriInfo = mockUriInfo()

        val interceptor = new OffsetPaginatedListLinksWriterInterceptor
        interceptor.uriInfo = uriInfo

        val context = mock[AbstractWriterInterceptorContext]
        when(context.setEntity(any())).thenCallRealMethod()
        when(context.getEntity).thenCallRealMethod()

        val entity = List("a", "b", "c").asJava
        context.setEntity(entity)
        interceptor.aroundWriteTo(context)

        context.getEntity mustBe entity
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
