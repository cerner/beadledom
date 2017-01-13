package com.cerner.beadledom.avro

import com.cerner.beadledom.testing.JsonMatchers
import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.converter.ModelConverters
import com.wordnik.swagger.core.util.JsonSerializer
import org.scalatest.{BeforeAndAfterAll, FunSpec, ShouldMatchers}

/**
 * Spec tests for {@link SwaggerAvroModelConverter}.
 */
class SwaggerAvroModelConverterSpec
    extends FunSpec with ShouldMatchers with JsonMatchers with BeforeAndAfterAll {
  val converter = new SwaggerAvroModelConverter

  override def beforeAll = {
    ModelConverters.addConverter(converter, true)
  }

  override def afterAll = {
    ModelConverters.removeConverter(converter)
  }

  describe("SwaggerAvroModelConverter") {
    it("reads models correctly") {
      val expected = Resources.toString(
        getClass.getClassLoader.getResource("expected-happy-model.json"), Charsets.UTF_8)
      JsonSerializer.asJson(ModelConverters.readAll(classOf[HappyModel])) should equalJson(expected)
    }

    it("allows other converters to handle non-Avro models") {
      val expected =
        """
          |[ {
          |  "id" : "NonAvroModel",
          |  "description" : "Sooo boring.",
          |  "properties" : {
          |    "foo" : {
          |      "type" : "string"
          |    }
          |  }
          |} ]
        """.stripMargin
      JsonSerializer.asJson(ModelConverters.readAll(classOf[NonAvroModel])) should
          equalJson(expected)
    }
  }
}

@ApiModel(description = "Sooo boring.")
class NonAvroModel {
  val foo = "bar"
}
