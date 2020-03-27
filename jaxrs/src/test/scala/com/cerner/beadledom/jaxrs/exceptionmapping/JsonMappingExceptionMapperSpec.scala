package com.cerner.beadledom.jaxrs.exceptionmapping

import com.cerner.beadledom.jaxrs.provider.{FakeRepository, FakeResource}
import com.cerner.beadledom.json.common.model.JsonError
import com.cerner.beadledom.testing.JsonErrorMatchers._
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import javax.ws.rs.core.HttpHeaders.CONTENT_TYPE
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status._
import org.jboss.resteasy.mock.{MockDispatcherFactory, MockHttpRequest, MockHttpResponse}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Unit tests for [[JsonMappingExceptionMapper]].
 *
 * @author John Leacox
 */
class JsonMappingExceptionMapperSpec
    extends AnyFunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with MockitoSugar {

  val jsonMappingExceptionMapper = new JsonMappingExceptionMapper
  val fakeRepository = mock[FakeRepository]
  val fakeResource = new FakeResource(fakeRepository)
  val url = "/fakeResource/ExceptionEndpoint"
  val request = MockHttpRequest.get(url)
  var response: MockHttpResponse = _
  val dispatcher = MockDispatcherFactory.createDispatcher()

  override def beforeAll(): Unit = {
    dispatcher.getRegistry.addSingletonResource(fakeResource)
    dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])
    dispatcher.getProviderFactory.registerProvider(classOf[JsonMappingExceptionMapper])
  }

  before {
    Mockito.reset(fakeRepository)
    response = new MockHttpResponse
  }

  describe("JsonMappingExceptionMapper") {
    describe("when the service throws a JsonMappingException") {
      it("returns a Json response with bad request error and status 400") {
        val exception = new JsonMappingException("Exception Message")

        when(fakeRepository.fakeMethod()).thenThrow(exception)

        dispatcher.invoke(request, response)

        response.getStatus mustBe BAD_REQUEST.getStatusCode
        response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
        response.getContentAsString must beBadRequestError("Unable to map request JSON")
      }
    }

    describe("#toResponse") {
      it("is mapped to a Json response with bad request error and status 400") {
        val exception = new JsonMappingException("Exception Message")

        val response = jsonMappingExceptionMapper.toResponse(exception)

        response.getStatus mustBe BAD_REQUEST.getStatusCode
        response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

        val jsonError = response.getEntity.asInstanceOf[JsonError]
        jsonError.code mustBe BAD_REQUEST.getStatusCode
        jsonError.message mustBe "Unable to map request JSON"
      }
    }
  }
}
