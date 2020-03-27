package com.cerner.beadledom.guice.dynamicbindings

import com.google.inject.{AbstractModule, Guice, Key, TypeLiteral}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[DynamicBindingProviderImpl]].
  *
  * @author John Leacox
  */
class DynamicBindingProviderImplSpec extends AnyFunSpec with Matchers {
  describe("DynamicBindingProvider") {
    describe("#get") {
      it("returns the same instance for a singleton binding across multiple calls") {
        val injector = Guice.createInjector(new AbstractModule() {
          override def configure(): Unit = {
            bind(classOf[String]).toInstance("Hello World")
            bind(classOf[StringWrapper])
                .annotatedWith(classOf[TestBindingAnnotation]).to(classOf[StringWrapper])
                .asEagerSingleton()

            DynamicAnnotations
                .bindDynamicProvider(binder(), classOf[StringWrapper],
                  classOf[TestBindingAnnotation])
          }
        })

        val typeLiteral = new TypeLiteral[DynamicBindingProvider[StringWrapper]]() {}

        val dynamicProvider = injector.getInstance(Key.get(typeLiteral))
        val first = dynamicProvider.get(classOf[TestBindingAnnotation])
        val second = dynamicProvider.get(classOf[TestBindingAnnotation])

        first mustBe second
      }

      it("returns a different instance for a non-singleton binding across multiple calls") {
        val injector = Guice.createInjector(new AbstractModule() {
          override def configure(): Unit = {
            bind(classOf[String]).toInstance("Hello World")
            bind(classOf[StringWrapper])
                .annotatedWith(classOf[TestBindingAnnotation]).to(classOf[StringWrapper])

            DynamicAnnotations
                .bindDynamicProvider(binder(), classOf[StringWrapper],
                  classOf[TestBindingAnnotation])
          }
        })

        val typeLiteral = new TypeLiteral[DynamicBindingProvider[StringWrapper]]() {}
        val dynamicProvider = injector.getInstance(Key.get(typeLiteral))
        val first = dynamicProvider.get(classOf[TestBindingAnnotation])
        val second = dynamicProvider.get(classOf[TestBindingAnnotation])

        first must not be second
      }
    }
  }
}
