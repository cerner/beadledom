package com.cerner.beadledom.jaxrs.exceptionmappers

import com.cerner.beadledom.jaxrs.models.JsonError

import org.scalatest.{FunSpec, ShouldMatchers}

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status._

/**
  * Spec tests for {@link ThrowableExceptionMapper}.
  *
  * @author Cal Fisher
  */
class ThrowableExceptionMapperSpec extends FunSpec with ShouldMatchers {

  val throwableExceptionMapper = new ThrowableExceptionMapper

  describe("ThrowableExceptionMapper") {
    describe("when a RuntimeException is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new RuntimeException("Exception Message")

        val response = throwableExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when a generic Exception is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new Exception("Exception Message")

        val response = throwableExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when a Throwable exception is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new Throwable("Exception Message")

        val response = throwableExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when an unhandled custom exception is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new CustomException("Exception Message")

        val response = throwableExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }
  }
}

class CustomException(val message: String) extends Exception(message)
