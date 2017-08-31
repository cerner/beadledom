package com.cerner.beadledom.jaxrs.exceptionmappers

import com.cerner.beadledom.jaxrs.models.JsonError

import org.jboss.resteasy.spi.{Failure, LoggableFailure, ReaderException, WriterException}
import org.scalatest.{FunSpec, ShouldMatchers}

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status._

/**
  * Unit tests for {@link FailureExceptionMapper}.
  *
  * @author Cal Fisher
  */
class FailureExceptionMapperSpec extends FunSpec with ShouldMatchers {

  val failureExceptionMapper = new FailureExceptionMapper

  describe("FailureExceptionMapper") {
    describe("when a ReaderException is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new ReaderException("Exception Message")

        val response = failureExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when a WriterException is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new WriterException("Exception Message")

        val response = failureExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when a LoggableFailure exception is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new LoggableFailure("Exception Message")

        val response = failureExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }

    describe("when a Failure exception is thrown") {
      it("is mapped to a Json response with internal server error and status 500") {
        val exception = new Failure("Exception Message")

        val response = failureExceptionMapper.toResponse(exception)

        response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
        jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
      }
    }
  }
}
