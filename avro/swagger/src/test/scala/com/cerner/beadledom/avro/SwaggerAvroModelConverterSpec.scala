package com.cerner.beadledom.avro

import java.io.ByteArrayOutputStream

import com.cerner.beadledom.testing.JsonMatchers
import com.google.common.base.Charsets
import com.google.common.io.Resources
import io.swagger.converter.ModelConverters
import io.swagger.jaxrs.listing.SwaggerSerializers
import io.swagger.models.Swagger
import javax.ws.rs.core.MediaType
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters

/**
 * Spec tests for {@link SwaggerAvroModelConverter}.
 */
class SwaggerAvroModelConverterSpec
    extends FunSpec with Matchers with JsonMatchers with BeforeAndAfterAll {
  val converter = new SwaggerAvroModelConverter

  override def beforeAll = {
//    ModelConverters.getInstance().addConverter(converter)
  }

  override def afterAll = {
    ModelConverters.getInstance().removeConverter(converter)
  }

  describe("SwaggerAvroModelConverter") {
//    it("reads models correctly") {
//      val expected = Resources.toString(
//        getClass.getClassLoader.getResource("expected-happy-model.json"), Charsets.UTF_8)
//
//      JsonSerializer[HappyModel]
//      JsonSerializer.asJson(ModelConverters.readAll(classOf[HappyModel])) should equalJson(expected)
//    }

    it("allows other converters to handle non-Avro models") {
      val expected = Resources.toString(
              getClass.getClassLoader.getResource("expected-non-avro-model.json"), Charsets.UTF_8)

      val os = new ByteArrayOutputStream()

      val models = ModelConverters.getInstance().readAll(classOf[NonAvroModel])

      val swagger = new Swagger()

      models.size() shouldBe 1

      for ((key, value) <- JavaConverters.mapAsScalaMapConverter(models).asScala) {
        swagger.model(key, value)
      }

      val c = classOf[NonAvroModel]

      new SwaggerSerializers().writeTo(swagger, c, null, classOf[NonAvroModel].getAnnotations, MediaType.APPLICATION_JSON_TYPE, null, os)

      os.toString("UTF-8") should
        equalJson(expected)
      }
  }
}
