package com.cerner.beadledom.jaxrs

import com.cerner.beadledom.testing.UnitSpec
import java.lang.annotation.Annotation
import java.net.URI
import java.util
import java.util.{Collections, Date, Locale}
import javax.ws.rs.core._

import org.mockito.Mockito._
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/**
  * Spec tests for [[DelegatingGenericResponse]].
  *
  * @author John Leacox
  */
class DelegatingGenericResponseSpec
    extends UnitSpec with MockitoSugar with ScalaCheckDrivenPropertyChecks {
  describe("DelegatingGenericResponse") {
    describe("#create") {
      it("creates a new instance of DelegatingGenericResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response mustBe a[DelegatingGenericResponse[_]]
      }
    }

    describe("#isSuccessful") {
      it("returns true for status code in range [200..299)") {
        forAll(Gen.choose(200, 299)) { (code) =>
          val rawResponse = mock[Response]
          when(rawResponse.getStatus).thenReturn(code)

          val response = DelegatingGenericResponse.create(null, rawResponse)
          response.isSuccessful mustBe true
        }
      }

      it("returns false for status code in range [100..199) or [300..599)") {
        forAll(Gen.oneOf(Gen.choose(100, 199), Gen.choose(300, 599))) { (code) =>
          val rawResponse = mock[Response]
          when(rawResponse.getStatus).thenReturn(code)

          val response = DelegatingGenericResponse.create(null, rawResponse)
          response.isSuccessful mustBe false
        }
      }
    }

    describe("#body") {
      it("returns the response body") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.body mustBe "Hello World"
      }

      it("returns null when no body is present") {
        val rawResponse = mock[Response]
        val response: GenericResponse[String] = DelegatingGenericResponse.create(null, rawResponse)

        response.body mustBe null
      }
    }

    describe("#errorBody") {
      it("returns the error response body") {
        val rawResponse = mock[Response]
        when(rawResponse.getStatus).thenReturn(400)
        when(rawResponse.readEntity(classOf[String])).thenReturn("Hello World")
        val response = DelegatingGenericResponse.create(null, rawResponse)

        response.errorBody.string() mustBe "Hello World"
      }

      it("returns null when the response is not an error") {
        val rawResponse = mock[Response]
        when(rawResponse.getStatus).thenReturn(200)
        val response: GenericResponse[String] = DelegatingGenericResponse.create(null, rawResponse)

        response.errorBody mustBe null
      }
    }

    describe("#getEntityClass") {
      it("returns the response body class") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getEntityClass mustBe classOf[String]
      }

      it("returns null when no body is present") {
        val rawResponse = mock[Response]
        val response: DelegatingGenericResponse[String] = DelegatingGenericResponse
            .create(null, rawResponse)

        response.getEntityClass mustBe null
      }
    }

    describe("#getGenericType") {
      it("returns the response body generic type") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getGenericType mustBe classOf[String]
      }

      it("returns null when no body is present") {
        val rawResponse = mock[Response]
        val response: DelegatingGenericResponse[String] = DelegatingGenericResponse
            .create(null, rawResponse)

        response.getEntityClass mustBe null
      }
    }

    describe("#getStatus") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getStatus
        verify(rawResponse, atLeastOnce()).getStatus
      }
    }

    describe("#getStatusInfo") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getStatusInfo
        verify(rawResponse).getStatusInfo
      }
    }

    describe("#getentity") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getEntity
        verify(rawResponse).getEntity
      }
    }

    describe("#readEntity(Class<U> entityType)") {
      it("throws an IllegalArgumentException if entityType does not match the body type") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        intercept[IllegalArgumentException] {
          response.readEntity(classOf[Integer])
        }
      }

      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.readEntity(classOf[String])
        verify(rawResponse).readEntity(classOf[String])
      }
    }

    describe("#readEntity(GenericType<U> entityType)") {
      it("throws an IllegalArgumentException if entityType does not match the body type") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        val genericType = new GenericType(classOf[Integer])
        intercept[IllegalArgumentException] {
          response.readEntity(genericType)
        }
      }

      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        val genericType = new GenericType(classOf[String])
        response.readEntity(genericType)
        verify(rawResponse).readEntity(genericType)
        // This is a real oddity, but I don't want to waste more than the 20 minutes or so I've
        // already spent troubleshooting this. For some reason having the above `verify` line
        // as the last line in this test leads to a NullPointerException, but having any line after
        // it avoids the NullPointerException and still executes the verifying code.
        true mustBe true
      }
    }

    describe("#readEntity(Class<U> entityType, Annotation[] annotations)") {
      it("throws an IllegalArgumentException if entityType does not match the body type") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        intercept[IllegalArgumentException] {
          response.readEntity(classOf[Integer], Array[Annotation]())
        }
      }

      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.readEntity(classOf[String], Array[Annotation]())
        verify(rawResponse).readEntity(classOf[String], Array[Annotation]())
      }
    }

    describe("#readEntity(entityType: GenericType[U], annotations: Array[Annotation])") {
      it("throws an IllegalArgumentException if entityType does not match the body type") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        val genericType = new GenericType(classOf[Integer])
        intercept[IllegalArgumentException] {
          response.readEntity(genericType, Array[Annotation]())
        }
      }

      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        val genericType = new GenericType(classOf[String])
        response.readEntity(genericType, Array[Annotation]())
        verify(rawResponse).readEntity(genericType, Array[Annotation]())
        // This is a real oddity, but I don't want to waste more than the 20 minutes or so I've
        // already spent troubleshooting this. For some reason having the above `verify` line
        // as the last line in this test leads to a NullPointerException, but having any line after
        // it avoids the NullPointerException and still executes the verifying code.
        true mustBe true
      }
    }

    describe("#hasEntity") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.hasEntity
        verify(rawResponse).hasEntity
      }
    }

    describe("#bufferEntity") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.bufferEntity
        verify(rawResponse).bufferEntity
      }
    }

    describe("#close") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.close()
        verify(rawResponse).close()
      }
    }

    describe("#getMediaType") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getMediaType
        verify(rawResponse).getMediaType
      }
    }

    describe("#getLanguage") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getLanguage
        verify(rawResponse).getLanguage
      }
    }

    describe("#getLength") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getLength
        verify(rawResponse).getLength
      }
    }

    describe("#getAllowedMethods") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getAllowedMethods
        verify(rawResponse).getAllowedMethods
      }
    }

    describe("#getCookies") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getCookies
        verify(rawResponse).getCookies
      }
    }

    describe("#getEntityTag") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getEntityTag
        verify(rawResponse).getEntityTag
      }
    }

    describe("#getDate") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getDate
        verify(rawResponse).getDate
      }
    }

    describe("#getLastModified") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getLastModified
        verify(rawResponse).getLastModified
      }
    }

    describe("#getLocation") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getLocation
        verify(rawResponse).getLocation
      }
    }

    describe("#getLinks") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getLinks
        verify(rawResponse).getLinks
      }
    }

    describe("#hasLink") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.hasLink("link")
        verify(rawResponse).hasLink("link")
      }
    }

    describe("#getLink") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getLink("link")
        verify(rawResponse).getLink("link")
      }
    }

    describe("#getLinkBuilder") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getLinkBuilder("link")
        verify(rawResponse).getLinkBuilder("link")
      }
    }

    describe("#getMetadata") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getMetadata
        verify(rawResponse).getMetadata
      }
    }

    describe("#getHeaders") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getHeaders
        verify(rawResponse).getHeaders
      }
    }

    describe("#getStringHeaders") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getStringHeaders
        verify(rawResponse).getStringHeaders
      }
    }

    describe("#getHeaderString") {
      it("delegates to the underlying rawResponse") {
        val rawResponse = mock[Response]
        val response = DelegatingGenericResponse.create("Hello World", rawResponse)

        response.getHeaderString("header")
        verify(rawResponse).getHeaderString("header")
      }
    }

    describe("#toString") {
      it("returns String with all fields included") {
        val rawResponse = mock[Response]
        val date = new Date(0L)

        when(rawResponse.getStatus).thenReturn(200)
        when(rawResponse.getMediaType).thenReturn(MediaType.APPLICATION_JSON_TYPE)
        when(rawResponse.getDate).thenReturn(date)
        when(rawResponse.getLength).thenReturn(123)
        when(rawResponse.getLastModified).thenReturn(date)
        when(rawResponse.getEntityTag).thenReturn(new EntityTag("tag-value"))
        when(rawResponse.getLanguage).thenReturn(Locale.ENGLISH)
        when(rawResponse.getLocation).thenReturn(new URI("http://localhost"))
        val headers  = new MultivaluedHashMap[String, Object]()
        headers.put("header-key", util.Arrays.asList("header-value"))
        when(rawResponse.getHeaders).thenReturn(headers)
        when(rawResponse.getCookies).thenReturn(Collections.singletonMap("my-cookie", new NewCookie("key", "value")))
        when(rawResponse.getLinks).thenReturn(new util.HashSet[Link]())

        DelegatingGenericResponse.create("Hello World", rawResponse).toString mustBe
          s"""DelegatingGenericResponse{body=Hello World, bodyClass=class java.lang.String, errorBody=null,
             |rawResponse=Response{status=200, mediaType=application/json, date=$date, length=123,
             |lastModified=$date, entityTag="tag-value", language=en, location=http://localhost,
             |headers={header-key=[header-value]}, cookies={my-cookie=key=value;Version=1}, links=[] } }""".stripMargin.replaceAll("\n", " ")
      }
    }

  }
}
