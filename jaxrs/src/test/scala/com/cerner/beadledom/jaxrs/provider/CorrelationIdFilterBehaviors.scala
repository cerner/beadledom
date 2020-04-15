package com.cerner.beadledom.jaxrs.provider

import com.cerner.beadledom.correlation.CorrelationContext
import javax.ws.rs.container.{ContainerRequestContext, ContainerResponseContext}
import org.jboss.resteasy.core.Headers
import org.mockito
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.slf4j.MDC

/**
 * @author John Leacox
 */
trait CorrelationIdFilterBehaviors extends BeforeAndAfter with Matchers {
  this: FunSpec =>

  def correlationIdFilter(filter: CorrelationIdFilter, headerName: String,
      mdcName: String, correlationContext: CorrelationContext): Unit = {
    it("adds the correlation id to the MDC, CorrelationContext, and request properties") {
      val request = Mockito.mock(classOf[ContainerRequestContext])
      Mockito.when(request.getHeaderString(headerName))
          .thenReturn("123")

      filter.filter(request)

      MDC.get(mdcName) should be("123")
      Mockito.verify(request).setProperty(mdcName, "123")
      correlationContext.getId should be("123")
    }

    it("adds a new correlation id to the MDC and CorrelationContext when correlation id not present on the request header")
    {
      val request = Mockito.mock(classOf[ContainerRequestContext])
      Mockito.when(request.getHeaderString(headerName))
          .thenReturn(null)

      filter.filter(request)

      val captor = ArgumentCaptor.forClass(classOf[String])
      Mockito.verify(request)
          .setProperty(mockito.ArgumentMatchers.eq(mdcName), captor.capture())
      MDC.get(mdcName) should be(captor.getValue)
      correlationContext.getId should be(captor.getValue)
    }

    it("adds the correlation id to the response header") {
      val request = Mockito.mock(classOf[ContainerRequestContext])
      Mockito.when(request.getProperty(mdcName))
          .thenReturn("123", Nil: _*)

      val response = Mockito.mock(classOf[ContainerResponseContext])
      val headers = Mockito.mock(classOf[Headers[AnyRef]])
      Mockito.when(response.getHeaders).thenReturn(headers)

      filter.filter(request, response)

      MDC.get(mdcName) should be(null)
      Mockito.verify(headers).add(headerName, "123")
    }

    it("adds a new correlation id to the response header when not present") {
      val request = Mockito.mock(classOf[ContainerRequestContext])
      Mockito.when(request.getProperty(mdcName))
          .thenReturn(null, Nil: _*)

      val response = Mockito.mock(classOf[ContainerResponseContext])
      val headers = Mockito.mock(classOf[Headers[AnyRef]])
      Mockito.when(response.getHeaders).thenReturn(headers)

      filter.filter(request, response)

      MDC.get(mdcName) should be(null)
      Mockito.verify(headers).add(mockito.ArgumentMatchers.eq(headerName),
        mockito.ArgumentMatchers.anyString())
    }
  }
}
