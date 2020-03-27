package com.cerner.beadledom.jaxrs

import java.net.URI
import java.util.Locale
import javax.ws.rs.core.Response.StatusType
import javax.ws.rs.core.{EntityTag, MediaType, Response, Variant}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Spec tests for [[GenericResponses]].
  *
  * @author John Leacox
  */
class GenericResponsesSpec extends AnyFunSpec with Matchers {
  describe("GenericResponses") {
    describe("#fromResponse") {
      it("creates a new response with the same values (shallow copy) of the existing response") {
        val original = new TestingGenericResponseBuilderFactory().create(200, "Hello")
            .header("test", "hi").build()
        val copy = GenericResponses.fromResponse(original).build()

        copy must not be original
        copy.getStatus mustBe original.getStatus
        copy.body() mustBe original.body()
        copy.getHeaders mustBe original.getHeaders
      }
    }

    describe("#status") {
      it("creates a new response with a status type") {
        val okStatus = new StatusType {
          override def getStatusCode: Int = 200

          override def getReasonPhrase: String = "Ok"

          override def getFamily = Response.Status.Family.SUCCESSFUL
        }

        val response = GenericResponses.status(okStatus).build()
        response.getStatus mustBe 200
        response.getStatusInfo mustBe Response.Status.OK
      }

      it("creates a new response with a status") {
        val response = GenericResponses.status(Response.Status.OK).build()
        response.getStatus mustBe 200
        response.getStatusInfo mustBe Response.Status.OK
      }

      it("creates a new response with a status code") {
        val response = GenericResponses.status(200).build()
        response.getStatus mustBe 200
        response.getStatusInfo mustBe Response.Status.OK
      }

      it("creates a new response with a status code and body") {
        val response = GenericResponses.status(200, "Hello World").build()
        response.getStatus mustBe 200
        response.getStatusInfo mustBe Response.Status.OK

        response.body() mustBe "Hello World"
      }
    }

    describe("#ok") {
      it("creates a new response with OK status") {
        val response = GenericResponses.ok().build()
        response.getStatus mustBe 200
      }

      it("creates a new response with OK status and body") {
        val response = GenericResponses.ok("Hello World").build()
        response.getStatus mustBe 200
        response.body mustBe "Hello World"
      }

      it("creates a new response with OK status, body, and mediatype from String") {
        val response = GenericResponses.ok("Hello World", "application/json").build()
        response.getStatus mustBe 200
        response.body mustBe "Hello World"
        response.getMediaType mustBe MediaType.APPLICATION_JSON_TYPE
      }

      it("creates a new response with OK status, body, and mediatype") {
        val response = GenericResponses.ok("Hello World", MediaType.APPLICATION_JSON_TYPE).build()
        response.getStatus mustBe 200
        response.body mustBe "Hello World"
        response.getMediaType mustBe MediaType.APPLICATION_JSON_TYPE
      }

      it("creates a new response with OK status, body, and Variant") {
        val variant = new Variant(MediaType.APPLICATION_JSON_TYPE, Locale.ENGLISH, "test-encoding")

        val response = GenericResponses.ok("Hello World", variant).build()
        response.getStatus mustBe 200
        response.body mustBe "Hello World"
        response.getMediaType mustBe MediaType.APPLICATION_JSON_TYPE
        response.getLanguage mustBe Locale.ENGLISH
        response.getHeaderString("Content-Encoding") mustBe "test-encoding"
      }
    }

    describe("#serverError") {
      it("creates a new response with a 500 status code") {
        val response = GenericResponses.serverError().build()
        response.getStatus mustBe 500
      }
    }

    describe("#created") {
      it("creates a new response with a 201 status code and body") {
        val response = GenericResponses.created("Hello World", new URI("/location")).build()
        response.getStatus mustBe 201
        response.body mustBe "Hello World"
        response.getLocation.toString mustBe "/location"
      }
    }

    describe("#accapted") {
      it("creates a new response with a 202 status code") {
        val response = GenericResponses.accepted().build()
        response.getStatus mustBe 202
      }

      it("creates a new response with a 202 status code and body") {
        val response = GenericResponses.accepted("Hello World").build()
        response.getStatus mustBe 202
        response.body() mustBe "Hello World"
      }
    }

    describe("#noContent") {
      it("creates a new response with a 204 status code") {
        val response = GenericResponses.noContent().build()
        response.getStatus mustBe 204
      }
    }

    describe("#notModified") {
      it("creates a new response with a 304 status code") {
        val response = GenericResponses.notModified().build()
        response.getStatus mustBe 304
      }

      it("creates a new response with a 304 status code and entity tag string") {
        val response = GenericResponses.notModified("etag").build()
        response.getStatus mustBe 304
        response.getEntityTag mustBe new EntityTag("etag")
      }

      it("creates a new response with a 304 status code and entity tag") {
        val response = GenericResponses.notModified(new EntityTag("etag")).build()
        response.getStatus mustBe 304
        response.getEntityTag mustBe new EntityTag("etag")
      }
    }

    describe("#seeOther") {
      it("creates a new response with 303 status code and URI location") {
        val response = GenericResponses.seeOther(new URI("/other")).build()
        response.getStatus mustBe 303
        response.getLocation.toString mustBe "/other"
      }
    }

    describe("#temporaryRedirect") {
      it("creates a new response with 307 status code and URI location") {
        val response = GenericResponses.temporaryRedirect(new URI("/temp")).build()
        response.getStatus mustBe 307
        response.getLocation.toString mustBe "/temp"
      }
    }

    describe("#notAcceptable") {
      it("creates a new response with 406 status code and a Vary header") {
        val variants = Variant
            .mediaTypes(MediaType.APPLICATION_JSON_TYPE, MediaType.TEXT_PLAIN_TYPE).build()

        val response = GenericResponses.notAcceptable(variants).build()
        response.getStatus mustBe 406
        response.getHeaderString("Vary") mustBe "Accept"
      }
    }
  }
}
