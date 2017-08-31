package com.cerner.beadledom.jaxrs.exceptionmappers

import com.cerner.beadledom.jaxrs.models.JsonError

import org.scalatest.{FunSpec, ShouldMatchers}

import javax.ws.rs._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.{MediaType, Response}

/**
 * Unit tests for {@link WebApplicationExceptionMapper}.
 *
 * @author Mayank Singh
 * @author Cal Fisher
 */
class WebApplicationExceptionMapperSpec extends FunSpec with ShouldMatchers {

  val webApplicationExceptionMapper = new WebApplicationExceptionMapper

  describe("WebApplicationExceptionMapper") {
    describe("when a WebApplicationException is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new WebApplicationException()

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when a custom WebApplicationException with status 400 is thrown") {
      it("is mapped to a Json response with bad request error and status 400") {
        val exceptionResponse = Response
            .status(BAD_REQUEST)
            .`type`(MediaType.APPLICATION_JSON)
            .build
        val exception = new WebApplicationException("Exception Message", exceptionResponse)

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe BAD_REQUEST.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe BAD_REQUEST.getStatusCode
        jsonError.message shouldBe BAD_REQUEST.getReasonPhrase
      }
    }

    describe("when a custom WebApplicationException with status 401 is thrown") {
      it("is mapped to a Json response with unauthorized error and status 401") {
        val exceptionResponse = Response
            .status(UNAUTHORIZED)
            .`type`(MediaType.APPLICATION_JSON)
            .build
        val exception = new WebApplicationException("Exception Message", exceptionResponse)

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe UNAUTHORIZED.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe UNAUTHORIZED.getStatusCode
        jsonError.message shouldBe UNAUTHORIZED.getReasonPhrase
      }
    }

    describe("when a custom WebApplicationException with status 403 is thrown") {
      it("is mapped to a Json response with forbidden error and status 403") {
        val exceptionResponse = Response
            .status(FORBIDDEN)
            .`type`(MediaType.APPLICATION_JSON)
            .build
        val exception = new WebApplicationException("Exception Message", exceptionResponse)

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe FORBIDDEN.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe FORBIDDEN.getStatusCode
        jsonError.message shouldBe FORBIDDEN.getReasonPhrase
      }
    }

    describe("when a custom WebApplicationException with status 404 is thrown") {
      it("is mapped to a Json response with not found error and status 404") {
        val exceptionResponse = Response
            .status(NOT_FOUND)
            .`type`(MediaType.APPLICATION_JSON)
            .build
        val exception = new WebApplicationException("Exception Message", exceptionResponse)

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe NOT_FOUND.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe NOT_FOUND.getStatusCode
        jsonError.message shouldBe NOT_FOUND.getReasonPhrase
      }
    }

    describe("when a custom WebApplicationException with status 500 is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exceptionResponse = Response
            .status(INTERNAL_SERVER_ERROR)
            .`type`(MediaType.TEXT_PLAIN)
            .build
        val exception = new WebApplicationException("Exception Message", exceptionResponse)

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase

      }
    }

    describe("when a BadRequestException is thrown") {
      it("is mapped to a Json response with bad request error and status 400") {
        val exception = new BadRequestException("Exception Message")

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe BAD_REQUEST.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe BAD_REQUEST.getStatusCode
        jsonError.message shouldBe BAD_REQUEST.getReasonPhrase
      }
    }

    describe("when a NotAuthorizedException is thrown") {
      it("is mapped to a Json response with unauthorized error and status 401") {
        val exception = new NotAuthorizedException("Exception Message")

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe UNAUTHORIZED.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe UNAUTHORIZED.getStatusCode
        jsonError.message shouldBe UNAUTHORIZED.getReasonPhrase
      }
    }

    describe("when a ForbiddenException is thrown") {
      it("is mapped to a Json response with forbidden error and status 403") {
        val exception = new ForbiddenException("Exception Message")

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe FORBIDDEN.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe FORBIDDEN.getStatusCode
        jsonError.message shouldBe FORBIDDEN.getReasonPhrase
      }
    }

    describe("when a NotFoundException is thrown") {
      it("is mapped to a Json response with not found error and status 404") {
        val exception = new NotFoundException("Exception Message")

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe NOT_FOUND.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe NOT_FOUND.getStatusCode
        jsonError.message shouldBe NOT_FOUND.getReasonPhrase
      }
    }

    describe("when an InternalServerErrorException is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new InternalServerErrorException("Exception Message")

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when custom WebApplicationException is thrown with `text/plain` mediatype") {
      it("is mapped to a Json response with the same status and `application/json` mediatype") {
        val exceptionResponse = Response
            .status(INTERNAL_SERVER_ERROR)
            .`type`(MediaType.TEXT_PLAIN)
            .build
        val exception = new WebApplicationException("Exception Message", exceptionResponse)

        val response = webApplicationExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }
  }
}
