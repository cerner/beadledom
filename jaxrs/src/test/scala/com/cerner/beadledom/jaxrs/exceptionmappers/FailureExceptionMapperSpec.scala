package com.cerner.beadledom.jaxrs.exceptionmappers

import com.cerner.beadledom.jaxrs.models.JsonError
import com.cerner.beadledom.jaxrs.provider.{FakeRepository, FakeResource}

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

import org.jboss.resteasy.mock.{MockDispatcherFactory, MockHttpRequest, MockHttpResponse}
import org.jboss.resteasy.spi.{Failure, LoggableFailure, ReaderException, WriterException}
import org.mockito.Mockito
import org.mockito.Mockito._

import play.api.libs.json.Json

import javax.ws.rs.core.HttpHeaders._
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status._

/**
  * Unit tests for [[FailureExceptionMapper]].
  *
  * @author Cal Fisher
  */
class FailureExceptionMapperSpec extends BaseExceptionMapperSpec {

  val failureExceptionMapper = new FailureExceptionMapper
  val fakeRepository = mock[FakeRepository]
  val fakeResource = new FakeResource(fakeRepository)
  val url = "/fakeResource/ExceptionEndpoint"
  val request = MockHttpRequest.get(url)
  var response: MockHttpResponse = _
  val internalServerErrorJsonError = createJsonError(INTERNAL_SERVER_ERROR)

  before {
    Mockito.reset(fakeRepository)
    response = new MockHttpResponse
  }

  describe("FailureExceptionMapper") {
    describe("unit testing the behavior of ThrowableExceptionMapper class when an exception is " +
        "thrown from a resource") {
      val dispatcher = MockDispatcherFactory.createDispatcher()
      dispatcher.getRegistry.addSingletonResource(fakeResource)
      dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])
      dispatcher.getProviderFactory.registerProvider(classOf[FailureExceptionMapper])

      describe("when the service throws a ReaderException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new ReaderException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) mustBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a WriterException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new WriterException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) mustBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a LoggableFailure exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new LoggableFailure("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) mustBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a Failure exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new Failure("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) mustBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a custom Failure exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new CustomFailureException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) mustBe internalServerErrorJsonError
        }
      }
    }

    describe("unit testing the FailureExceptionMapper class") {
      describe("when a ReaderException is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new ReaderException("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a WriterException is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new WriterException("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a LoggableFailure exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new LoggableFailure("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a Failure exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new Failure("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a custom Failure exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new CustomFailureException("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }
    }
  }
}

class CustomFailureException(val message: String) extends Failure(message)
