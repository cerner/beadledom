package com.cerner.beadledom.resteasy

import org.jboss.resteasy.spi.{Registry, ResteasyProviderFactory}
import org.scalatest.{FunSpec, MustMatchers}
import org.scalatest.mockito.MockitoSugar
import com.cerner.beadledom.guice.dynamicbindings.AnnotatedModule
import com.google.inject.Guice

class ModuleProcessorSpec extends FunSpec with MockitoSugar with MustMatchers{

  val registry: Registry = mock[Registry]
  val providerFactory: ResteasyProviderFactory = mock[ResteasyProviderFactory]

  describe("ModuleProcessor#new") {
    it("throws a NullPointerException for null registry") {
      intercept[NullPointerException] {
        new ModuleProcessor(null, providerFactory)
      }
    }

    it("throws a NullPointerException for null provider factory") {
      intercept[NullPointerException] {
        new ModuleProcessor(registry, null)
      }
    }
  }

  describe("ModuleProcessor#processInjector") {
    it("throws a NullPointerException for null injector") {
      intercept[NullPointerException] {
        new ModuleProcessor(registry, providerFactory).processInjector(null)
      }
    }

    it("excludes providers that have a binding annotation") {
      val injector = Guice.createInjector(new AnnotatedModule(classOf[TestBindingAnnotation]) {
        override def configure(): Unit = {
          bind(classOf[TestJaxRsProvider]).annotatedWith(getBindingAnnotation).toProvider(
            new TestProviderWithBindingAnnotation(getBindingAnnotation)
          )
          expose(classOf[TestJaxRsProvider]).annotatedWith(getBindingAnnotation)
        }
      })
      val providerFactory = new ResteasyProviderFactory()
      new ModuleProcessor(registry, providerFactory).processInjector(injector)

      providerFactory.getProviderInstances mustBe empty
    }

    it("excludes providers that have a binding annotation and registers providers without a binding annotation") {
      val injector = Guice.createInjector(new AnnotatedModule(classOf[TestBindingAnnotation]) {
        override def configure(): Unit = {
          bind(classOf[TestJaxRsProvider]).annotatedWith(getBindingAnnotation).toProvider(
            new TestProviderWithBindingAnnotation(getBindingAnnotation)
          )
          expose(classOf[TestJaxRsProvider]).annotatedWith(getBindingAnnotation)

          bind(classOf[TestJaxRsProvider]).toProvider(new TestProvider())
          expose(classOf[TestJaxRsProvider])
        }
      })
      val providerFactory = new ResteasyProviderFactory()
      new ModuleProcessor(registry, providerFactory).processInjector(injector)

      providerFactory.getProviderInstances must have size 1
    }
  }
}



