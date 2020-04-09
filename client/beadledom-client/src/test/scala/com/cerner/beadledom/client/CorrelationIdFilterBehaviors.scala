package com.cerner.beadledom.client.jaxrs

import java.util.UUID
import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.core.MultivaluedHashMap
import com.cerner.beadledom.client.CorrelationIdFilter
import org.mockito.Mockito
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.{BeMatcher, MatchResult}
import scala.util.Try
import org.scalatest.matchers.should.Matchers

/**
  * Testing Behaviors for the {@link CorrelationIdFilter}
  *
  * @author John Leacox
  */
trait CorrelationIdFilterBehaviors extends Matchers {
  this: AnyFunSpec =>

  val aUUID =
    BeMatcher { (left: Any) =>
      MatchResult(
        Try(UUID.fromString(left.asInstanceOf[String])).isSuccess,
        s"Was not a UUID:\n$left",
        s"Was a UUID:\n$left")
    }

  def correlationIdFilter(filter: CorrelationIdFilter,
      testCorrelationIdContext: TestCorrelationIdContext, headerName: String): Unit = {
    it("adds the existing correlation id to the request header") {
      testCorrelationIdContext.setCorrelationId("123")

      val headers = new MultivaluedHashMap[String, Object]()
      val request = Mockito.mock(classOf[ClientRequestContext])
      Mockito.when(request.getHeaders).thenReturn(headers)

      filter.filter(request)

      request.getHeaders.getFirst(headerName) shouldBe "123"
    }

    it("adds a new correlation id to the request header when not present") {
      testCorrelationIdContext.setCorrelationId(null)

      val headers = new MultivaluedHashMap[String, Object]()
      val request = Mockito.mock(classOf[ClientRequestContext])
      Mockito.when(request.getHeaders).thenReturn(headers)

      filter.filter(request)

      request.getHeaders.getFirst(headerName) shouldBe aUUID
    }
  }
}
