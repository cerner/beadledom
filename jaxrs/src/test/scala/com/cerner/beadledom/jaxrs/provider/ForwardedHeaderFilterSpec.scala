package com.cerner.beadledom.jaxrs.provider

import java.util

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.{MultivaluedHashMap, MultivaluedMap, SecurityContext, UriInfo}
import org.mockito.Mockito
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.scalatest.mockito.MockitoSugar

class ForwardedHeaderFilterSpec extends FunSpec with BeforeAndAfter with Matchers
  with MockitoSugar {

  it("makes the request https") {
    val request = Mockito.mock(classOf[ContainerRequestContext])
    val securityContext = Mockito.mock(classOf[SecurityContext])
    Mockito.when(securityContext.isSecure).thenReturn(false)
    Mockito.when(request.getSecurityContext).thenReturn(securityContext)
    val headerMap: MultivaluedMap[String,String] = new MultivaluedHashMap[String,String]
    headerMap.add("Forwarded", "proto=http")
    headerMap.add("X-Forwarded-Proto","https")
    Mockito.when(request.getHeaders).thenReturn(headerMap)
    Mockito.when(request.getUriInfo).thenReturn(Mockito.mock(classOf[UriInfo]))

    val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
    forwardedHeaderFilter.filter(request)

    Mockito.verify(request).getSecurityContext.isSecure.equals(true)
  }
}
