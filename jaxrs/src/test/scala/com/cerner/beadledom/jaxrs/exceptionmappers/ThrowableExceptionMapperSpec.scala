package com.cerner.beadledom.jaxrs.exceptionmappers

import com.cerner.beadledom.jaxrs.models.JsonError
import com.cerner.beadledom.jaxrs.provider.{FakeDao, FakeResource}

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

import org.jboss.resteasy.mock._
import org.mockito.Mockito
import org.mockito.Mockito._

import play.api.libs.json.Json

import javax.ws.rs.core.HttpHeaders.CONTENT_TYPE
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status._

/**
  * Unit tests for {@link ThrowableExceptionMapper}.
  *
  * @author Cal Fisher
  */
class ThrowableExceptionMapperSpec extends BaseExceptionMapperSpec {

  val throwableExceptionMapper = new ThrowableExceptionMapper
  val fakeDao = mock[FakeDao]
  val fakeResource = new FakeResource(fakeDao)
  val url = "/fakeResource/ExceptionEndpoint"
  val request = MockHttpRequest.get(url)
  var response: MockHttpResponse = _
  val internalServerErrorJsonError = createJsonError(INTERNAL_SERVER_ERROR)

  override def beforeEach {
    Mockito.reset(fakeDao)
    response = new MockHttpResponse
  }

  describe("ThrowableExceptionMapper") {
    describe("unit testing the behavior of ThrowableExceptionMapper class when an exception is " +
        "thrown from a resource") {
      val dispatcher = MockDispatcherFactory.createDispatcher()
      dispatcher.getRegistry.addSingletonResource(fakeResource)
      dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])
      dispatcher.getProviderFactory.registerProvider(classOf[ThrowableExceptionMapper])

      describe("when the service throws a RuntimeException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new RuntimeException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a generic Exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new Exception("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a custom Exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new CustomException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
        }
      }
    }

    describe("unit testing the ThrowableExceptionMapper class") {
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
}

class CustomException(val message: String) extends Exception(message)
