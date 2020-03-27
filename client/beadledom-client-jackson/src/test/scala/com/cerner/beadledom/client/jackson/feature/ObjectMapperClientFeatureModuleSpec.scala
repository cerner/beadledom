package com.cerner.beadledom.client.jackson.feature

import com.cerner.beadledom.client.jackson.ObjectMapperClientFeatureModule
import com.cerner.beadledom.client.jackson.test.annotations.TestBindingAnnotation
import com.cerner.beadledom.jackson.SerializationFeatureFlag
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.google.inject.multibindings.ProvidesIntoSet
import com.google.inject.{AbstractModule, Guice, Key, Module}
import org.scalatest._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Unit tests for ObjectMapperClientFeatureModule.
 *
 * @author John Leacox
 */
class ObjectMapperClientFeatureModuleSpec
    extends AnyFunSpec with BeforeAndAfter with Matchers with MockitoSugar {
  var testModule: Module = _

  before {
    testModule = new AbstractModule() {
      override def configure(): Unit = {
        install(ObjectMapperClientFeatureModule.`with`(classOf[TestBindingAnnotation]))
      }
    }
  }
  describe("ObjectMapperClientFeatureModule") {
    describe("#with") {
      it("throws an IllegalArgumentException for non-binding annotations") {
        intercept[IllegalArgumentException] {
          ObjectMapperClientFeatureModule.`with`(classOf[Override])
        }
      }
    }

    it("provides an annotated OAuth1ClientFilterFeature") {
      val injector = Guice.createInjector(testModule)
      val feature = injector
          .getInstance(Key.get(classOf[JacksonJsonProvider], classOf[TestBindingAnnotation]))
      feature should not be null
    }

    it("provides ObjectMapper with default configuration") {
      val injector = Guice.createInjector(testModule)
      val mapper = injector
          .getInstance(Key.get(classOf[ObjectMapper], classOf[TestBindingAnnotation]))

      mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) should be(false)
      mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) should be(false)
    }

    it("provides ObjectMapper with customized features") {
      val jacksonModule = new AbstractModule {
        override def configure(): Unit = {
          install(testModule)
        }

        @ProvidesIntoSet
        @TestBindingAnnotation
        def getSerializationFeature(): SerializationFeatureFlag = {
          SerializationFeatureFlag.create(SerializationFeature.INDENT_OUTPUT, true)
        }
      }

      val injector = Guice.createInjector(jacksonModule)
      val mapper = injector
          .getInstance(Key.get(classOf[ObjectMapper], classOf[TestBindingAnnotation]))

      mapper.isEnabled(SerializationFeature.INDENT_OUTPUT) should be(true)
    }
  }
}
