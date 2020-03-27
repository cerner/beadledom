package com.cerner.beadledom.jaxrs.exceptionmapping

import com.cerner.beadledom.jaxrs.provider.{FakeRepository, FakeResource}
import com.cerner.beadledom.json.common.model.JsonError
import com.cerner.beadledom.testing.JsonErrorMatchers._

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

import org.jboss.resteasy.mock._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

import javax.ws.rs.core.HttpHeaders.CONTENT_TYPE
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[ThrowableExceptionMapper]].
  *
  * @author Cal Fisher
  */
class ThrowableExceptionMapperSpec
    extends AnyFunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with MockitoSugar {

  val throwableExceptionMapper = new ThrowableExceptionMapper
  val fakeRepository = mock[FakeRepository]
  val fakeResource = new FakeResource(fakeRepository)
  val url = "/fakeResource/ExceptionEndpoint"
  val request = MockHttpRequest.get(url)
  var response: MockHttpResponse = _
  val dispatcher = MockDispatcherFactory.createDispatcher()

  override def beforeAll(): Unit = {
    dispatcher.getRegistry.addSingletonResource(fakeResource)
    dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])
    dispatcher.getProviderFactory.registerProvider(classOf[ThrowableExceptionMapper])
  }

  before {
    Mockito.reset(fakeRepository)
    response = new MockHttpResponse
  }

  describe("ThrowableExceptionMapper") {
    describe("when an exception is thrown from a resource") {
      describe("when the service throws a RuntimeException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new RuntimeException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when the service throws a generic Exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new Exception("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when the service throws a custom Exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new CustomException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }
    }

    describe("unit testing the ThrowableExceptionMapper class") {
      describe("when a RuntimeException is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new RuntimeException("Exception Message")

          val response = throwableExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a generic Exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new Exception("Exception Message")

          val response = throwableExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when an unhandled custom exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new CustomException("Exception Message")

          val response = throwableExceptionMapper.toResponse(exception)

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

class CustomException(val message: String) extends Exception(message)
