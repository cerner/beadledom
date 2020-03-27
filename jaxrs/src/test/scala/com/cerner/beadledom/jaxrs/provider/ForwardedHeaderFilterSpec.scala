package com.cerner.beadledom.jaxrs.provider

import java.net.URI

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core._
import org.mockito.Mockito.{when,verify}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ForwardedHeaderFilterSpec extends AnyFunSpec with BeforeAndAfter with Matchers with BeforeAndAfterEach
  with MockitoSugar {

  def createContainerRequestContext: ContainerRequestContext = {
    val request = mock[ContainerRequestContext]
    val uriInfo = mock[UriInfo]
    when(uriInfo.getRequestUriBuilder).thenReturn(UriBuilder.fromUri(new URI("http://hello.there")))
    when(request.getUriInfo).thenReturn(uriInfo)
    request
  }

  describe("For a request with just the X-Forwarded-Proto header") {
    describe("when the header has a value of https") {
      it("changes the request to https") {
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)
        when(request.getHeaderString("Forwarded")).thenReturn(null)
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("https")
        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("https://hello.there"))
      }
    }

    describe("when the header has a value other than https") {
      it("keeps the request http") {
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)
        when(request.getHeaderString("Forwarded")).thenReturn(null)
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("other")
        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("http://hello.there"))
      }
    }

    describe("when the header is an empty string") {
      it("keeps the request http") {
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)
        when(request.getHeaderString("Forwarded")).thenReturn(null)
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("")
        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("http://hello.there"))
      }
    }
  }

  describe("For a request with just the Forwarded header"){
    describe("with multiple values"){
      val forwardedHeaderMultiValuedString = "for=192.0.2.60;by=203.0.113.43"
      describe("if the protocol is set to https"){
        it("changes the request to https"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          when(securityContext.isSecure).thenReturn(false)
          when(request.getSecurityContext).thenReturn(securityContext)
          when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

          when(request.getHeaderString("Forwarded"))
            .thenReturn(forwardedHeaderMultiValuedString.concat(";proto=https"))

          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          verify(request).setRequestUri(new URI("https://hello.there"))
        }
      }

      describe("if the protocol is not set to https"){
        it("keeps the request http"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          when(securityContext.isSecure).thenReturn(false)
          when(request.getSecurityContext).thenReturn(securityContext)
          when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

          when(request.getHeaderString("Forwarded"))
            .thenReturn(forwardedHeaderMultiValuedString.concat(";proto=other"))

          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          verify(request).setRequestUri(new URI("http://hello.there"))
        }
      }

      describe("if no protocol is set"){
        it("keeps the request http"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          when(securityContext.isSecure).thenReturn(false)
          when(request.getSecurityContext).thenReturn(securityContext)
          when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

          when(request.getHeaderString("Forwarded"))
            .thenReturn(forwardedHeaderMultiValuedString)

          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          verify(request).setRequestUri(new URI("http://hello.there"))
        }
      }
    }

    describe("with just a protocol value"){
      describe("if the protocol is set to https"){
        it("changes the request to https"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          when(securityContext.isSecure).thenReturn(false)
          when(request.getSecurityContext).thenReturn(securityContext)
          when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)
          when(request.getHeaderString("Forwarded")).thenReturn("proto=https")


          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          verify(request).setRequestUri(new URI("https://hello.there"))
        }
      }

      describe("if the protocol is not set to https"){
        it("keeps the request http"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          when(securityContext.isSecure).thenReturn(false)
          when(request.getSecurityContext).thenReturn(securityContext)
          when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)
          when(request.getHeaderString("Forwarded")).thenReturn("proto=other")

          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          verify(request).setRequestUri(new URI("http://hello.there"))
        }
      }
    }

    describe("with no protocol value"){
      it("keeps the request http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

        when(request.getHeaderString("Forwarded")).thenReturn("for=192.0.2.60;by=203.0.113.43")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("http://hello.there"))
      }
    }

    describe("that is empty"){
      it("keeps the request http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

        when(request.getHeaderString("Forwarded")).thenReturn("")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("http://hello.there"))
      }
    }
  }

  describe("For a request with both the X-Forwarded-Proto and Forwarded headers"){
    describe("if both headers permit https"){
      it("changes the request to https"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)
        when(request.getHeaderString("Forwarded")).thenReturn("proto=https")
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("https")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("https://hello.there"))
      }
    }

    describe("if both headers don't permit https"){
      it("keeps the request as http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)
        when(request.getHeaderString("Forwarded")).thenReturn("proto=http")
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("http")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("http://hello.there"))
      }
    }
  }

  describe("For a request with no headers"){
    describe("for a security context that returns false"){
      it("keeps the request as http"){
        val request = createContainerRequestContext
        when(request.getHeaderString("Forwarded")).thenReturn(null)
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)
        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(false)
        when(request.getSecurityContext).thenReturn(securityContext)

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("http://hello.there"))
      }
    }

    describe("for a security context that returns true"){
      it("changes the request to https"){
        val request = createContainerRequestContext
        when(request.getHeaderString("Forwarded")).thenReturn(null)
        when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

        val securityContext = mock[SecurityContext]
        when(securityContext.isSecure).thenReturn(true)
        when(request.getSecurityContext).thenReturn(securityContext)

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        verify(request).setRequestUri(new URI("https://hello.there"))
      }
    }
  }
}
