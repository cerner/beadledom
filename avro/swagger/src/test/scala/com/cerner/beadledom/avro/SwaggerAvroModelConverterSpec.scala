package com.cerner.beadledom.avro

import java.io.ByteArrayOutputStream

import com.cerner.beadledom.testing.JsonMatchers
import com.google.common.base.Charsets
import com.google.common.io.Resources
import io.swagger.converter.ModelConverters
import io.swagger.jaxrs.listing.SwaggerSerializers
import io.swagger.models.Swagger
import javax.ws.rs.core.MediaType
import org.scalatest.BeforeAndAfterAll

import scala.collection.JavaConverters
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Spec tests for {@link SwaggerAvroModelConverter}.
 */
class SwaggerAvroModelConverterSpec
  extends AnyFunSpec with Matchers with JsonMatchers with BeforeAndAfterAll {
  val converter = new SwaggerAvroModelConverter

  override def beforeAll = {
    ModelConverters.getInstance().addConverter(converter)
  }

  override def afterAll = {
    ModelConverters.getInstance().removeConverter(converter)
  }

  private def serialize[T](clazz: Class[T]): String = {
    val os = new ByteArrayOutputStream()

    val models = ModelConverters.getInstance().readAll(clazz)

    val swagger = new Swagger()
    for ((key, value) <- JavaConverters.mapAsScalaMapConverter(models).asScala) {
      swagger.addDefinition(key, value)
    }

    new SwaggerSerializers().writeTo(swagger, null, null, null, MediaType.APPLICATION_JSON_TYPE, null, os)

    os.toString("UTF-8")
  }

  describe("SwaggerAvroModelConverter") {
    it("reads models correctly") {
      val expected = Resources.toString(
        getClass.getClassLoader.getResource("expected-happy-model.json"), Charsets.UTF_8)

      serialize(classOf[HappyModel]) should equalJson(expected)
    }

    it("allows other converters to handle non-Avro models") {
      val expected = Resources.toString(
        getClass.getClassLoader.getResource("expected-non-avro-model.json"), Charsets.UTF_8)

      serialize(classOf[NonAvroModel]) should equalJson(expected)
    }
  }
}
