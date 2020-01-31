package com.cerner.beadledom.avro

import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.util

import com.cerner.beadledom.testing.UnitSpec
import com.google.inject.multibindings.ProvidesIntoSet
import com.google.inject.{AbstractModule, Guice, Key, TypeLiteral}
import io.swagger.converter.{ModelConverter, ModelConverterContext}
import io.swagger.models.Model
import io.swagger.models.properties.Property

/**
  * Spec tests for {@link AvroSwaggerGuiceModule}
  */
class AvroSwaggerGuiceModuleSpec extends UnitSpec {
  val avroSwaggerMockModule = new AbstractModule {
    override def configure(): Unit = {
      install(new AvroSwaggerGuiceModule)
    }

    @ProvidesIntoSet
    def getAvroSwaggerModel(): ModelConverter = {
      new Dependency1
    }
  }

  describe("AvroSwaggerGuiceModule") {
    it("Adds dependencies to the multibinder") {
      val setType = new TypeLiteral[java.util.Set[ModelConverter]] {}
      val injector = Guice.createInjector(avroSwaggerMockModule)
      val dependencies = injector.getInstance(Key.get(setType))

      dependencies must have size 2
    }
  }
}

class Dependency1 extends ModelConverter {
  override def resolve(`type`: Type, context: ModelConverterContext, chain: util.Iterator[ModelConverter]): Model = null

  override def resolveProperty(`type`: Type, context: ModelConverterContext, annotations: Array[Annotation], chain: util.Iterator[ModelConverter]): Property = null
}
