package com.cerner.beadledom.jaxrs.provider

import java.net.URI

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core._
import org.mockito.Mockito
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.scalatest.mockito.MockitoSugar

class ForwardedHeaderFilterSpec extends FunSpec with BeforeAndAfter with Matchers
  with MockitoSugar {

  it("makes the request https if the X-Forwarded-Proto is set to https") {
    val request = mock[ContainerRequestContext]

    val uriInfo = mock[UriInfo]
    Mockito.when(uriInfo.getBaseUriBuilder).thenReturn(UriBuilder.fromUri(new URI("http://hello.there")))
    Mockito.when(request.getUriInfo).thenReturn(uriInfo)

    val securityContext = mock[SecurityContext]
    Mockito.when(securityContext.isSecure).thenReturn(false)
    Mockito.when(request.getSecurityContext).thenReturn(securityContext)

    val headerMap: MultivaluedMap[String,String] = new MultivaluedHashMap[String,String]
    headerMap.add("Forwarded", "proto=http")
    headerMap.add("X-Forwarded-Proto","https")
    Mockito.when(request.getHeaders).thenReturn(headerMap)

    val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
    forwardedHeaderFilter.filter(request)

    Mockito.verify(request).setRequestUri(new URI("https://hello.there"))
  }

  it("makes the request https if the Forwarded protocol is set to https"){
    val request = mock[ContainerRequestContext]

    val uriInfo = mock[UriInfo]
    Mockito.when(uriInfo.getBaseUriBuilder).thenReturn(UriBuilder.fromUri(new URI("http://hello.there")))
    Mockito.when(request.getUriInfo).thenReturn(uriInfo)

    val securityContext = mock[SecurityContext]
    Mockito.when(securityContext.isSecure).thenReturn(false)
    Mockito.when(request.getSecurityContext).thenReturn(securityContext)

    val headerMap: MultivaluedMap[String,String] = new MultivaluedHashMap[String,String]
    headerMap.add("Forwarded", "proto=https")
    headerMap.add("X-Forwarded-Proto","http")
    Mockito.when(request.getHeaders).thenReturn(headerMap)

    val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
    forwardedHeaderFilter.filter(request)

    Mockito.verify(request).setRequestUri(new URI("https://hello.there"))
  }

  it("makes the request https if the SecurityContext.isSecure returns true"){
    val request = mock[ContainerRequestContext]

    val uriInfo = mock[UriInfo]
    Mockito.when(uriInfo.getBaseUriBuilder).thenReturn(UriBuilder.fromUri(new URI("http://hello.there")))
    Mockito.when(request.getUriInfo).thenReturn(uriInfo)

    val securityContext = mock[SecurityContext]
    Mockito.when(securityContext.isSecure).thenReturn(true)
    Mockito.when(request.getSecurityContext).thenReturn(securityContext)

    val headerMap: MultivaluedMap[String,String] = new MultivaluedHashMap[String,String]
    headerMap.add("Forwarded", "proto=http")
    headerMap.add("X-Forwarded-Proto","https")
    Mockito.when(request.getHeaders).thenReturn(headerMap)

    val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
    forwardedHeaderFilter.filter(request)

    Mockito.verify(request).setRequestUri(new URI("https://hello.there"))
  }
}
