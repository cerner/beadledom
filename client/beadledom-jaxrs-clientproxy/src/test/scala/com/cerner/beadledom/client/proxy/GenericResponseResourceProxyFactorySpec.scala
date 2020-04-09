package com.cerner.beadledom.client.proxy

import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import javax.ws.rs.core.Response

import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * @author John Leacox
  */
class GenericResponseResourceProxyFactorySpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("GenericResponseResourceProxyFactory") {
    describe("#proxy") {
      it("it creates a delegate proxy with GenericResponse returns type replaced with Response") {
        var delegateProxy: Any = null
        val delegateFactory = new ResourceProxyFactory {
          override def proxy[T](proxyInterface: Class[T]): T = {
            val response = mock[Response]
            delegateProxy = Mockito.mock(proxyInterface, new Answer[Response] {
              override def answer(invocation: InvocationOnMock): Response = {
                if (classOf[Response].equals(invocation.getMethod.getReturnType)) {
                  response
                } else {
                  null
                }
              }
            })

            delegateProxy.asInstanceOf[T]
          }
        }

        val factory = new GenericResponseResourceProxyFactory(delegateFactory)
        val resource = factory.proxy(classOf[GenericTestingResource])
        resource.getGeneric

        val mockingDetails = Mockito.mockingDetails(delegateProxy)
        val invocations = mockingDetails.getInvocations.asScala
        invocations.head.getMethod.getReturnType mustBe classOf[Response]
      }

    }
  }
}
