package com.cerner.beadledom.guice.dynamicbindings

import com.google.inject._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[AnnotatedModule]].
  *
  * @author John Leacox
  */
class AnnotatedModuleSpec extends AnyFunSpec with Matchers {
  describe("AnnotatedModule") {
    describe("constructor") {
      it("throws an IllegalArgumentException for a non-binding annotation") {
        intercept[IllegalArgumentException] {
          new AnnotatedModule(classOf[Override]) {
            override def configure(): Unit = ???
          }
        }
      }

      it("does not throw an exception and returns the annotation for a BindingAnnotation") {
        new AnnotatedModule(classOf[TestBindingAnnotation]) {
          override def configure(): Unit = ???
        }
      }

      it("does not throw an exception and returns the annotation for a Qualifier") {
        new AnnotatedModule(classOf[TestQualifier]) {
          override def configure(): Unit = ???
        }
      }
    }

    describe("#getBindingAnnotation") {
      it("returns the modules binding annotation") {
        val module = new AnnotatedModule(classOf[TestBindingAnnotation]) {
          override def configure(): Unit = ???
        }

        module.getBindingAnnotation mustBe classOf[TestBindingAnnotation]
      }
    }

    describe("#bindDynamicProvider(Class)") {
      it("it does not automatically expose bound dynamic providers") {
        val injector = Guice.createInjector(new AnnotatedModule(classOf[TestBindingAnnotation]) {
          override def configure(): Unit = {
            bind(classOf[String]).annotatedWith(getBindingAnnotation).toInstance("Hello World")

            bindDynamicProvider(classOf[String])
          }
        })

        val typeLiteral = new TypeLiteral[DynamicBindingProvider[String]]() {}

        intercept[ConfigurationException] {
          injector.getInstance(Key.get(typeLiteral))
        }
      }
    }

    describe("#bindDynamicProvider(TypeLiteral)") {
      it("it does not automatically expose bound dynamic providers") {
        val injector = Guice.createInjector(new AnnotatedModule(classOf[TestBindingAnnotation]) {
          override def configure(): Unit = {
            bind(classOf[String]).toInstance("Hello World")

            val typeLiteral = new TypeLiteral[StringWrapper](){}
            bind(typeLiteral)
                .annotatedWith(getBindingAnnotation).to(classOf[StringWrapper]).asEagerSingleton()

            bindDynamicProvider(typeLiteral)
          }
        })

        val typeLiteral = new TypeLiteral[DynamicBindingProvider[StringWrapper]]() {}

        intercept[ConfigurationException] {
          injector.getInstance(Key.get(typeLiteral))
        }
      }
    }
  }
}
