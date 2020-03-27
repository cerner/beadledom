package com.cerner.beadledom.client.proxy


import javax.ws.rs.core.Response
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * @author John Leacox
  */
class GenericResponseResourceTransformerSpec extends AnyFunSpec with Matchers {
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
