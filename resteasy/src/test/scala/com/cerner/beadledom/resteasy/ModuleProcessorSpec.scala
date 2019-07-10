package com.cerner.beadledom.resteasy

import com.cerner.beadledom.resteasy.fauxservice.FauxModule
import org.jboss.resteasy.spi.{Registry, ResteasyProviderFactory}
import org.scalatest.{FunSpec, MustMatchers}
import org.scalatest.mockito.MockitoSugar
import com.google.inject.{AbstractModule, Guice}

class ModuleProcessorSpec extends FunSpec with MockitoSugar with MustMatchers{

  val registry: Registry = mock[Registry]
  val providerFactory: ResteasyProviderFactory = mock[ResteasyProviderFactory]

  describe("ModuleProcessor") {

    describe("constructor") {
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

    describe("processInjector") {
      it("throws a NullPointerException for null injector") {
        intercept[NullPointerException] {
          new ModuleProcessor(registry, providerFactory).processInjector(null)
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
        new ModuleProcessor(registry, providerFactory).processInjector(injector)

        providerFactory.getProviderInstances mustBe empty
      }

      it("excludes provider with binding annotation when two providers of the same type are bound with the injector") {
        val injector = Guice.createInjector(new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[TestJaxRsProvider]).annotatedWith(classOf[TestBindingAnnotation]).to(
              classOf[TestJaxRsProvider]
            )

            bind(classOf[TestJaxRsProvider]).toProvider(new TestProvider())
          }
        })
        val providerFactory = new ResteasyProviderFactory()
        new ModuleProcessor(registry, providerFactory).processInjector(injector)

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
        new ModuleProcessor(registry, providerFactory).processInjector(injector)

        providerFactory.getProviderInstances must have size 1
      }
    }
  }
}
