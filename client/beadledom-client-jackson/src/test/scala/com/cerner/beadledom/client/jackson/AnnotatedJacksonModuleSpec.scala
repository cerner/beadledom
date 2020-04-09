package com.cerner.beadledom.client.jackson

import com.cerner.beadledom.client.jackson.test.annotations.TestBindingAnnotation
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationFeature, MapperFeature, ObjectMapper, SerializationFeature}
import com.google.inject.{Guice, Injector, Key}
import org.scalatest.BeforeAndAfter
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Specs for [[AnnotatedJacksonModule]].
 */
class AnnotatedJacksonModuleSpec
    extends AnyFunSpec with BeforeAndAfter with Matchers with MockitoSugar {
  var injector: Injector = _
  var mapper: ObjectMapper = _
  before {
    injector = Guice.createInjector(new AnnotatedJacksonTestModule)

    mapper = injector
        .getInstance(Key.get(classOf[ObjectMapper], classOf[TestBindingAnnotation]))
  }

  describe("Annotated JacksonModule") {
    describe("ObjectMapper") {
      it("includes bound features") {
        mapper.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) should be(true)
        mapper.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS) should be(true)
        mapper.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS) should be(false)
        mapper.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT) should be(true)
        mapper.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) should be(true)
        mapper.isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES) should be(true)
        mapper.isEnabled(JsonParser.Feature.ALLOW_COMMENTS) should be(true)
      }

      it("includes multibinder Jackson modules") {
        val modules = new ObjectMapperWrapper(mapper).getRegisteredModules
        modules.iterator().next() should be(new TestModule().getTypeId)
      }

      it("uses snake casing") {
        val model = new CamelCaseModel
        val str = mapper.writeValueAsString(model)
        str should include("long_field_name_with_camel_case")
        str should not include "longFieldNameWithCamelCase"
      }

      it("omits null fields when serializing") {
        val model = new CamelCaseModel
        model.longFieldNameWithCamelCase = null
        mapper.writeValueAsString(model) should not include "long_field_name_with_camel_case"
      }

      it("ignores unknown fields when no additional property map defined") {
        val input =
          """
            |{
            |  "field": "Captain Planet",
            |  "ignored_1": "he's a hero",
            |  "ignored_2": "he's gonna take pollution",
            |  "ignored_3": "down to zero"
            |}
          """.stripMargin

        val actual = mapper.readValue(input, classOf[NoAdditionalPropertyModel])
        actual.field should equal("Captain Planet")

        mapper.writeValueAsString(actual) should equal( """{"field":"Captain Planet"}""")
      }

      it("maps unknown fields to additional properties map when defined") {
        val input =
          """
            |{
            |  "field": "thunder...Thunder...THUNDERCATS",
            |  "ignored": "HHHHHHHOOOOOOOOOOO!"
            |}
          """.stripMargin

        val actual = mapper.readValue(input, classOf[AdditionalPropertyModel])
        actual.field should equal("thunder...Thunder...THUNDERCATS")

        actual.getAdditionalProperties should equal(Map("ignored" -> "HHHHHHHOOOOOOOOOOO!").asJava)

        mapper.writeValueAsString(actual) should equal(
          """{"field":"thunder...Thunder...THUNDERCATS","ignored":"HHHHHHHOOOOOOOOOOO!"}""")
      }
    }
  }
}
