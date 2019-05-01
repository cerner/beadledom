package com.cerner.beadledom.swagger

import com.cerner.beadledom.testing.UnitSpec
import com.google.inject.multibindings.{Multibinder, ProvidesIntoSet}
import com.google.inject.{AbstractModule, Guice, Key, TypeLiteral}
import com.wordnik.swagger.converter.ModelConverter
import com.wordnik.swagger.model.Model

/**
 * Spec tests for [[SwaggerModule]]
  */
class SwaggerModuleSpec extends UnitSpec {
  val swaggerMockModule = new AbstractModule {
    override def configure(): Unit = {
      install(new SwaggerModule)

      val swaggerModelConverterBinder: Multibinder[ModelConverter] = Multibinder
          .newSetBinder(binder, classOf[ModelConverter])

      swaggerModelConverterBinder.addBinding().to(classOf[Dependency1])
    }

    @ProvidesIntoSet
    def getSwaggerModelConverter1(): ModelConverter = {
      new Dependency2
    }
  }

  val swaggerMockModuleNoMultibinderDependency = new AbstractModule {
    override def configure(): Unit = {
      install(new SwaggerModule)
    }
  }

  describe("SwaggerModule") {
    it("Adds dependencies to the multibinder") {
      val setType = new TypeLiteral[java.util.Set[ModelConverter]] {}
      val injector = Guice.createInjector(swaggerMockModule)
      val dependencies = injector.getInstance(Key.get(setType))

      dependencies must have size 2
    }

    it("Creates empty multibinder if no dependencies exist") {
      val setType = new TypeLiteral[java.util.Set[ModelConverter]] {}
      val injector = Guice.createInjector(swaggerMockModuleNoMultibinderDependency)
      val dependencies = injector.getInstance(Key.get(setType))

      dependencies mustBe empty
    }
  }
}

class Dependency1 extends ModelConverter {
  override def toDescriptionOpt(cls: Class[_]): Option[String] = None
  override def toName(cls: Class[_]): String = "dep1"
  override def read(cls: Class[_], typeMap: Map[String, String]): Option[Model] = None
}

class Dependency2 extends ModelConverter {
  override def toDescriptionOpt(cls: Class[_]): Option[String] = None
  override def toName(cls: Class[_]): String = "dep2"
  override def read(cls: Class[_], typeMap: Map[String, String]): Option[Model] = None
}
