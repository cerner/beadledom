package com.cerner.beadledom.jaxrs.provider

import java.net.URI

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core._
import org.mockito.Mockito
import org.mockito.Mockito.reset
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FunSpec, Matchers}
import org.scalatest.mockito.MockitoSugar

class ForwardedHeaderFilterSpec extends FunSpec with BeforeAndAfter with Matchers with BeforeAndAfterEach
  with MockitoSugar {

  def createContainerRequestContext: ContainerRequestContext = {
    val request = mock[ContainerRequestContext]
    val uriInfo = mock[UriInfo]
    Mockito.when(uriInfo.getBaseUriBuilder).thenReturn(UriBuilder.fromUri(new URI("http://hello.there")))
    Mockito.when(request.getUriInfo).thenReturn(uriInfo)
    request
  }

  describe("For a request with just the X-Forwarded-Proto header"){
    describe("for a X-Forwarded-Proto header with https"){
      it("changes the request to https"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)
        Mockito.when(request.getHeaderString("Forwarded")).thenReturn(null)
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("https")
        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("https://hello.there"), null)
      }
    }

    describe("for a X-Forwarded-Proto header with a proto other than https"){
      it("keeps the request http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)
        Mockito.when(request.getHeaderString("Forwarded")).thenReturn(null)
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("other")
        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
      }
    }

    describe("for a X-Forwarded-Proto header thats an empty string"){
      it("keeps the request http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)
        Mockito.when(request.getHeaderString("Forwarded")).thenReturn(null)
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("")
        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
      }
    }
  }

  describe("For a request with just the Forwarded header"){
    describe("for a Forwarded header with mulitple values"){
      val forwardedHeaderMultiValuedString = "for=192.0.2.60;by=203.0.113.43"
      describe("if the protocol is set to https"){
        it("changes the request to https"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          Mockito.when(securityContext.isSecure).thenReturn(false)
          Mockito.when(request.getSecurityContext).thenReturn(securityContext)
          Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

          Mockito.when(request.getHeaderString("Forwarded"))
            .thenReturn(forwardedHeaderMultiValuedString.concat(";proto=https"))

          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          Mockito.verify(request).setRequestUri(new URI("https://hello.there"), null)
        }
      }

      describe("if the protocol is not set to https"){
        it("keeps the request http"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          Mockito.when(securityContext.isSecure).thenReturn(false)
          Mockito.when(request.getSecurityContext).thenReturn(securityContext)
          Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

          Mockito.when(request.getHeaderString("Forwarded"))
            .thenReturn(forwardedHeaderMultiValuedString.concat(";proto=other"))

          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
        }
      }

      describe("if no protocol is set"){
        it("keeps the request http"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          Mockito.when(securityContext.isSecure).thenReturn(false)
          Mockito.when(request.getSecurityContext).thenReturn(securityContext)
          Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

          Mockito.when(request.getHeaderString("Forwarded"))
            .thenReturn(forwardedHeaderMultiValuedString)

          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
        }
      }
    }

    describe("for a Forwarded header with just a protocol value"){
      describe("if the protocol is set to https"){
        it("changes the request to https"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          Mockito.when(securityContext.isSecure).thenReturn(false)
          Mockito.when(request.getSecurityContext).thenReturn(securityContext)
          Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)
          Mockito.when(request.getHeaderString("Forwarded")).thenReturn("proto=https")


          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          Mockito.verify(request).setRequestUri(new URI("https://hello.there"), null)
        }
      }

      describe("if the protocol is not set to https"){
        it("keeps the request http"){
          val request = createContainerRequestContext
          val securityContext = mock[SecurityContext]
          Mockito.when(securityContext.isSecure).thenReturn(false)
          Mockito.when(request.getSecurityContext).thenReturn(securityContext)
          Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)
          Mockito.when(request.getHeaderString("Forwarded")).thenReturn("proto=other")


          val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
          forwardedHeaderFilter.filter(request)

          Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
        }
      }
    }

    describe("for a Forwarded header with no protocol value"){
      it("keeps the request http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

        Mockito.when(request.getHeaderString("Forwarded")).thenReturn("for=192.0.2.60;by=203.0.113.43")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
      }
    }

    describe("for a Forwarded header with an empty string"){
      it("keeps the request http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

        Mockito.when(request.getHeaderString("Forwarded")).thenReturn("")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
      }
    }
  }

  describe("For a request with both the X-Forwarded-Proto and Forwarded headers"){
    describe("if both headers permit https"){
      it("changes the request to https"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)
        Mockito.when(request.getHeaderString("Forwarded")).thenReturn("proto=https")
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("https")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("https://hello.there"), null)
      }
    }

    describe("if both headers don't permit https"){
      it("keeps the request as http"){
        val request = createContainerRequestContext
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)
        Mockito.when(request.getHeaderString("Forwarded")).thenReturn("proto=http")
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn("http")

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
      }
    }
  }

  describe("For a request with no headers"){
    describe("for a security context that returns false"){
      it("keeps the request as http"){
        val request = createContainerRequestContext
        Mockito.when(request.getHeaderString("Forwarded")).thenReturn(null)
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)
        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(false)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("http://hello.there"), null)
      }
    }

    describe("for a security context that returns true"){
      it("changes the request to https"){
        val request = createContainerRequestContext
        Mockito.when(request.getHeaderString("Forwarded")).thenReturn(null)
        Mockito.when(request.getHeaderString("X-Forwarded-Proto")).thenReturn(null)

        val securityContext = mock[SecurityContext]
        Mockito.when(securityContext.isSecure).thenReturn(true)
        Mockito.when(request.getSecurityContext).thenReturn(securityContext)

        val forwardedHeaderFilter: ForwardedHeaderFilter = new ForwardedHeaderFilter
        forwardedHeaderFilter.filter(request)

        Mockito.verify(request).setRequestUri(new URI("https://hello.there"), null)
      }
    }
  }
}
