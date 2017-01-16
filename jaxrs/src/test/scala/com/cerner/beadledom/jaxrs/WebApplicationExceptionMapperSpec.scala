package com.cerner.beadledom.jaxrs

import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}
import org.scalatest.{FunSpec, ShouldMatchers}

/**
 * Spec tests for {@link PlainTextWebApplicationExceptionMapper}.
 *
 * @author Mayank Singh
 */
class WebApplicationExceptionMapperSpec extends FunSpec with ShouldMatchers {
  describe("PlainTextWebApplicationExceptionMapper") {
    it("creates response with 'text/plain' mediatype and exception entity") {
      val exception = new WebApplicationException("exception message")
      val webApplicationExceptionMapper = new PlainTextWebApplicationExceptionMapper()
      val response = webApplicationExceptionMapper.toResponse(exception)

      response.getStatus should be(500)
      response.getEntity should be("exception message")
      response.getMediaType should be(MediaType.TEXT_PLAIN_TYPE)
    }

    it("creates a response with existing mediatype if already present") {
      val originalResponse = Response.status(400).`type`(MediaType.APPLICATION_JSON).build()
      val exception = new WebApplicationException("exception message", originalResponse)
      val webApplicationExceptionMapper = new PlainTextWebApplicationExceptionMapper()
      val response = webApplicationExceptionMapper.toResponse(exception)

      response.getStatus should be(400)
      response.getEntity should be("exception message")
      response.getMediaType should be(MediaType.APPLICATION_JSON_TYPE)
    }

    it("creates a response with existing entity if already present") {
      val originalResponse = Response.status(400).entity("response message").build()
      val exception = new WebApplicationException("exception message", originalResponse)
      val webApplicationExceptionMapper = new PlainTextWebApplicationExceptionMapper()
      val response = webApplicationExceptionMapper.toResponse(exception)

      response.getStatus should be(400)
      response.getEntity should be("response message")
      response.getMediaType should be(MediaType.TEXT_PLAIN_TYPE)
    }

    it("creates a response for WebApplicationException subclasses") {
      val exception = new ForbiddenException("forbidden message")
      val webApplicationExceptionMapper = new PlainTextWebApplicationExceptionMapper()
      val response = webApplicationExceptionMapper.toResponse(exception)

      response.getStatus should be(403)
      response.getEntity should be("forbidden message")
      response.getMediaType should be(MediaType.TEXT_PLAIN_TYPE)
    }
  }
}
