package com.cerner.beadledom.guice.dynamicbindings

import com.google.inject._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[DynamicAnnotations]].
  *
  * @author John Leacox
  */
class DynamicAnnotationsSpec extends AnyFunSpec with Matchers {
  describe("DynamicAnnotations") {
    describe("#bindDynamicProvider") {
      describe("with a class") {
        it("requires a binding for the expected dynamic provider binding") {
          val moduleWithExpectedBinding = new AbstractModule() {
            override def configure(): Unit = {
              DynamicAnnotations
                  .bindDynamicProvider(binder(), classOf[String], classOf[TestBindingAnnotation])
            }
          }

          intercept[CreationException] {
            val injector = Guice.createInjector(moduleWithExpectedBinding)
          }
        }

        it("provides access to a binding by a dynamic binding annotation") {
          val moduleWithExpectedBinding = new AbstractModule() {
            override def configure(): Unit = {
              DynamicAnnotations
                  .bindDynamicProvider(binder(), classOf[String], classOf[TestBindingAnnotation])
              bind(classOf[String]).annotatedWith(classOf[TestBindingAnnotation])
                  .toInstance("Hello World")
            }
          }

          val injector = Guice.createInjector(moduleWithExpectedBinding)
          val dynamicProviderType = new TypeLiteral[DynamicBindingProvider[String]]() {}
          val provider = injector.getInstance(Key.get(dynamicProviderType))

          provider.get(classOf[TestBindingAnnotation]) mustBe "Hello World"
        }
      }

      describe("with a key") {
        it("requires a binding for the expected dynamic provider binding") {
          val moduleWithExpectedBinding = new AbstractModule() {
            override def configure(): Unit = {
              DynamicAnnotations.bindDynamicProvider(binder(),
                Key.get(classOf[String], classOf[TestBindingAnnotation]))
            }
          }

          intercept[CreationException] {
            val injector = Guice.createInjector(moduleWithExpectedBinding)
          }
        }

        it("provides access to a binding by a dynamic binding annotation") {
          val moduleWithExpectedBinding = new AbstractModule() {
            override def configure(): Unit = {
              DynamicAnnotations
                  .bindDynamicProvider(binder(),
                    Key.get(classOf[String], classOf[TestBindingAnnotation]))
              bind(classOf[String]).annotatedWith(classOf[TestBindingAnnotation])
                  .toInstance("Hello World")
            }
          }

          val injector = Guice.createInjector(moduleWithExpectedBinding)
          val dynamicProviderType = new TypeLiteral[DynamicBindingProvider[String]]() {}
          val provider = injector.getInstance(Key.get(dynamicProviderType))

          provider.get(classOf[TestBindingAnnotation]) mustBe "Hello World"
        }
      }
    }
  }
}
