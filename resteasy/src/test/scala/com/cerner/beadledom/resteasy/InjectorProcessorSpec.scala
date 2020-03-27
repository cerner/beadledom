package com.cerner.beadledom.resteasy

import org.jboss.resteasy.spi.{Registry, ResteasyProviderFactory}
import com.google.inject.{AbstractModule, Guice}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class InjectorProcessorSpec extends AnyFunSpec with MockitoSugar with Matchers{

  val registry: Registry = mock[Registry]
  val providerFactory: ResteasyProviderFactory = mock[ResteasyProviderFactory]

  describe("InjectorProcessor") {

    describe("constructor") {
      it("throws a NullPointerException for null registry") {
        intercept[NullPointerException] {
          new InjectorProcessor(null, providerFactory)
        }
      }

      it("throws a NullPointerException for null provider factory") {
        intercept[NullPointerException] {
          new InjectorProcessor(registry, null)
        }
      }
    }

    describe("processInjector") {
      it("throws a NullPointerException for null injector") {
        intercept[NullPointerException] {
          new InjectorProcessor(registry, providerFactory).process(null)
        }
      }

      it("excludes providers that have a binding annotation") {
        val injector = Guice.createInjector(new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[TestJaxRsProvider]).annotatedWith(classOf[TestBindingAnnotation]).to(
              classOf[TestJaxRsProvider]
            )
          }
        })
        val providerFactory = new ResteasyProviderFactory()
        new InjectorProcessor(registry, providerFactory).process(injector)

        providerFactory.getProviderInstances mustBe empty
      }

      it("excludes provider with binding annotation when two providers of the same type are bound with the injector") {
        val injector = Guice.createInjector(new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[TestJaxRsProvider]).annotatedWith(classOf[TestBindingAnnotation]).to(
              classOf[TestJaxRsProvider]
            )

            bind(classOf[TestJaxRsProvider])
          }
        })
        val providerFactory = new ResteasyProviderFactory()
        new InjectorProcessor(registry, providerFactory).process(injector)

        providerFactory.getProviderInstances must have size 1
      }

      it("excludes providers that have a binding annotation and registers providers without a binding annotation") {
        val injector = Guice.createInjector(new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[TestJaxRsProvider]).annotatedWith(classOf[TestBindingAnnotation]).to(
              classOf[TestJaxRsProvider]
            )

            bind(classOf[TestingExceptionMapper])
          }
        })
        val providerFactory = new ResteasyProviderFactory()
        new InjectorProcessor(registry, providerFactory).process(injector)

        providerFactory.getProviderInstances must have size 1
      }
    }
  }
}
