package com.cerner.beadledom.swagger2

import com.cerner.beadledom.testing.UnitSpec
import com.google.inject.multibindings.{Multibinder, ProvidesIntoSet}
import com.google.inject.{AbstractModule, Guice, Key, TypeLiteral}
import io.swagger.converter.{ModelConverter, ModelConverterContext}
import io.swagger.models.Model
import io.swagger.models.properties.Property
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.util

/**
 * Spec tests for [[Swagger2Module]].
 */
class SwaggerModuleSpec extends UnitSpec {
  val swaggerMockModule = new AbstractModule {
    override def configure(): Unit = {
      install(new Swagger2Module)

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
      install(new Swagger2Module)
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
  override def resolveProperty(`type`: Type,
      context: ModelConverterContext,
      annotations: Array[Annotation],
      chain: util.Iterator[ModelConverter]): Property = null
  override def resolve(`type`: Type,
      context: ModelConverterContext,
      chain: util.Iterator[ModelConverter]): Model = null
}

class Dependency2 extends ModelConverter {
  override def resolveProperty(`type`: Type,
      context: ModelConverterContext,
      annotations: Array[Annotation],
      chain: util.Iterator[ModelConverter]): Property = null
  override def resolve(`type`: Type,
      context: ModelConverterContext,
      chain: util.Iterator[ModelConverter]): Model = null
}
