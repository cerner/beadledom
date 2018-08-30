package com.cerner.beadledom.client.proxy

import com.cerner.beadledom.jaxrs.GenericResponse

import org.scalatest.{FunSpec, MustMatchers}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

import javax.ws.rs.core.{GenericType, Response}

/**
  * @author John Leacox
  */
class GenericClientProxySpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("GenericClientProxy") {
    describe("#invoke") {
      it("delegates to underlying transformed resource and wraps the result in a GenericResponse") {
        val mockResponse = mock[Response]
        when(mockResponse.readEntity(new GenericType(classOf[String]))).thenReturn("Hello World")
        when(mockResponse.getStatus).thenReturn(200)
        when(mockResponse.hasEntity).thenReturn(true)
        val underlyingProxy = mock[StandardTestingResource]
        when(underlyingProxy.getGeneric).thenReturn(mockResponse)

        val proxy = mock[GenericTestingResource]

        val clientProxy = new GenericClientProxy(underlyingProxy)

        val method = classOf[GenericTestingResource].getMethod("getGeneric")
        val args: Array[AnyRef] = Array[AnyRef]()
        val response = clientProxy.invoke(proxy, method, args)

        response mustBe a [GenericResponse[_]]
        val genericResponse = response.asInstanceOf[GenericResponse[String]]
        genericResponse.body() mustBe "Hello World"
      }

      it("delegates to underlying resource and returns a standard response") {
        val mockResponse = mock[Response]
        when(mockResponse.readEntity(classOf[String])).thenReturn("Hello World")
        when(mockResponse.getStatus).thenReturn(200)
        val underlyingProxy = mock[StandardTestingResource]
        when(underlyingProxy.getStandard).thenReturn(mockResponse)

        val proxy = mock[GenericTestingResource]

        val clientProxy = new GenericClientProxy(underlyingProxy)

        val method = classOf[GenericTestingResource].getMethod("getStandard")
        val args: Array[AnyRef] = Array[AnyRef]()
        val response = clientProxy.invoke(proxy, method, args)

        response mustBe a [Response]
        response.asInstanceOf[Response].readEntity(classOf[String]) mustBe "Hello World"
      }

      it("transform the underlying response with no entity into a GenericResponse") {
        val mockResponse = mock[Response]
        when(mockResponse.hasEntity).thenReturn(false)
        when(mockResponse.getStatus).thenReturn(204)
        val underlyingProxy = mock[StandardTestingResource]
        when(underlyingProxy.getGenericWithNoType).thenReturn(mockResponse)

        val proxy = mock[GenericTestingResource]

        val clientProxy = new GenericClientProxy(underlyingProxy)

        val method = classOf[GenericTestingResource].getMethod("getGenericWithNoType")
        val args: Array[AnyRef] = Array[AnyRef]()
        val response = clientProxy.invoke(proxy, method, args)

        response mustBe a [GenericResponse[_]]
        val genericResponse = response.asInstanceOf[GenericResponse[_]]
        genericResponse.body() mustBe null.asInstanceOf[Void]
        genericResponse.getStatus mustBe 204
      }
    }
  }
}
