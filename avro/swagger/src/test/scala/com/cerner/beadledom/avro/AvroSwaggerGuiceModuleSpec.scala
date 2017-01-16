package com.cerner.beadledom.avro

import com.cerner.beadledom.testing.UnitSpec
import com.google.inject.multibindings.ProvidesIntoSet
import com.google.inject.{AbstractModule, Guice, Key, TypeLiteral}
import com.wordnik.swagger.converter.ModelConverter
import com.wordnik.swagger.model.Model

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
  override def toDescriptionOpt(cls: Class[_]): Option[String] = None
  override def toName(cls: Class[_]): String = "dep1"
  override def read(cls: Class[_], typeMap: Map[String, String]): Option[Model] = None
}
