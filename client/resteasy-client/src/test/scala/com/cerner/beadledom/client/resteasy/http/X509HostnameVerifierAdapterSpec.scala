package com.cerner.beadledom.client.resteasy.http

import java.security.cert.X509Certificate
import javax.net.ssl.{HostnameVerifier, SSLException, SSLSession, SSLSocket}
import org.apache.http.conn.ssl.X509HostnameVerifier
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

/**
 * @author John Leacox
 */
class X509HostnameVerifierAdapterSpec
    extends FunSpec with Matchers with BeforeAndAfter with MockitoSugar {
  describe("X509HostnameVerifierAdapter") {
    describe("#adapt") {
      it("throws a NullPointerException if the adaptee is null") {
        intercept[NullPointerException] {
          X509HostnameVerifierAdapter.adapt(null)
        }
      }

      it("returns same instance when adaptee is already of type X509HostnameVerifier") {
        val verifier = mock[X509HostnameVerifier]
        X509HostnameVerifierAdapter.adapt(verifier) shouldBe verifier
      }

      it("returns an X509HostnameVerifier wrapper when the adaptee is a HostnameVerifier") {
        val verifier = mock[HostnameVerifier]
        X509HostnameVerifierAdapter.adapt(verifier)
            .isInstanceOf[X509HostnameVerifierAdapter] shouldBe true
      }
    }

    describe("#verify(String host, SSLSocket ssl)") {
      it("throws an SSLException if the delegate verify method returns false") {
        val host = "host"

        val sslSession = mock[SSLSession]
        val sslSocket = mock[SSLSocket]
        Mockito.when(sslSocket.getSession).thenReturn(sslSession)

        val verifier = mock[HostnameVerifier]
        Mockito.when(verifier.verify(host, sslSession)).thenReturn(false)

        intercept[SSLException] {
          X509HostnameVerifierAdapter.adapt(verifier).verify(host, sslSocket)
        }
      }

      it("does not throw an exception if the delegate verify method returns true") {
        val host = "host"

        val sslSession = mock[SSLSession]
        val sslSocket = mock[SSLSocket]
        Mockito.when(sslSocket.getSession).thenReturn(sslSession)

        val verifier = mock[HostnameVerifier]
        Mockito.when(verifier.verify(host, sslSession)).thenReturn(true)

        X509HostnameVerifierAdapter.adapt(verifier).verify(host, sslSocket)
      }
    }

    describe("#verify(String hostname, SSLSession session)") {
      it("returns false if the delegate verify method returns false") {
        val host = "host"
        val sslSession = mock[SSLSession]

        val verifier = mock[HostnameVerifier]
        Mockito.when(verifier.verify(host, sslSession)).thenReturn(false)

        X509HostnameVerifierAdapter.adapt(verifier).verify(host, sslSession) shouldBe false
      }

      it("returns true if the delegate verify method returns true") {
        val host = "host"
        val sslSession = mock[SSLSession]

        val verifier = mock[HostnameVerifier]
        Mockito.when(verifier.verify(host, sslSession)).thenReturn(true)

        X509HostnameVerifierAdapter.adapt(verifier).verify(host, sslSession) shouldBe true
      }
    }

    describe("#verify(String host, X509Certificate cert)") {
      it("throws an UnsupportedOperationException") {
        val host = "host"
        val cert = mock[X509Certificate]

        val verifier = mock[HostnameVerifier]

        intercept[UnsupportedOperationException] {
          X509HostnameVerifierAdapter.adapt(verifier).verify(host, cert)
        }
      }
    }

    describe("#verify(String host, String[] cns, String[] subjectAlts)") {
      it("throws an UnsupportedOperationException") {
        val host = "host"
        val cns = new Array[String](0)
        val subjectAlts = new Array[String](0)

        val verifier = mock[HostnameVerifier]

        intercept[UnsupportedOperationException] {
          X509HostnameVerifierAdapter.adapt(verifier).verify(host, cns, subjectAlts)
        }
      }
    }
  }
}
