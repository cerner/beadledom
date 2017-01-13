package com.cerner.beadledom.client.proxy

import org.scalatest.{FunSpec, MustMatchers}

import javax.ws.rs.core.Response

/**
  * @author John Leacox
  */
class GenericResponseResourceTransformerSpec extends FunSpec with MustMatchers {
  describe("GenericResponseResourceTransformer") {
    describe("#transform") {
      it ("transformers methods returning GenericResponse to Response") {
        val originalClass = classOf[GenericTestingResource]

        val transformer = new GenericResponseResourceTransformer
        val transformedClass = transformer.transform(originalClass)

        transformedClass.getMethod("getGeneric").getReturnType mustBe classOf[Response]
      }

      it ("transforms methods containing parameters") {
        val originalClass = classOf[GenericTestingResource]

        val originalMethod = originalClass.getMethod("getGenericWithParameter", classOf[String])
        val originalParamTypes = originalMethod.getParameterTypes
        val originalGenericParamTypes = originalMethod.getGenericParameterTypes

        val transformer = new GenericResponseResourceTransformer
        val transformedClass = transformer.transform(originalClass)

        transformedClass.getMethod("getGeneric").getReturnType mustBe classOf[Response]

        val method = transformedClass.getMethod("getGenericWithParameter", classOf[String])
        val paramTypes = method.getParameterTypes
        val genericParamTypes = method.getGenericParameterTypes

        paramTypes mustBe originalParamTypes
        genericParamTypes mustBe originalGenericParamTypes
      }
    }
  }
}
