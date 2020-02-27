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
    ModelConverters.getInstance().addConverter(converter)
  }

  override def afterAll = {
    ModelConverters.getInstance().removeConverter(converter)
  }

  private def serialize[T](clazz: Class[T]): String = {
    val os = new ByteArrayOutputStream()

    val models = ModelConverters.getInstance().read(clazz)

    val swagger = new Swagger()
    for ((key, value) <- JavaConverters.mapAsScalaMapConverter(models).asScala) {
      println(key)
      swagger.addDefinition(key, value)
    }

    println(swagger.getDefinitions)

    new SwaggerSerializers().writeTo(swagger, clazz, null, clazz.getAnnotations, MediaType.APPLICATION_JSON_TYPE, null, os)

    os.toString("UTF-8")
  }

  describe("SwaggerAvroModelConverter") {
    it("reads models correctly") {
      val expected = Resources.toString(
        getClass.getClassLoader.getResource("expected-happy-model.json"), Charsets.UTF_8)

      serialize(classOf[NestedHappyModel]) should equalJson(expected)
    }

    it("allows other converters to handle non-Avro models") {
      val expected = Resources.toString(
        getClass.getClassLoader.getResource("expected-non-avro-model.json"), Charsets.UTF_8)

      serialize(classOf[NonAvroModel]) should equalJson(expected)
    }
  }
}
