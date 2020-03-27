package com.cerner.beadledom.client

import com.google.inject._
import javax.ws.rs.ext
import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * @author John Leacox
  */
class BeadledomClientModuleSpec extends AnyFunSpec with Matchers with BeforeAndAfter {

  def getTestBindingInjector(modules: List[Module]): Injector = {
    val module = new AbstractModule {
      override def configure(): Unit = {
        install(BeadledomClientModule.`with`(classOf[TestBindingAnnotation]))
        modules.foreach(m => install(m))
      }
    }
    Guice.createInjector(module)
  }

  describe("BeadledomClientModule") {
    describe("#with") {
      it("throws an IllegalArgumentException for non-binding annotations") {
        intercept[IllegalArgumentException] {
          BeadledomClientModule.`with`(classOf[Override])
        }
      }

      it("returns a new BeadledomClientModule for a BindingAnnotation") {
        val module = BeadledomClientModule.`with`(classOf[TestBindingAnnotation])
        module must not be null
      }

      it("returns a new BeadledomClientModule for a Qualifier") {
        val module = BeadledomClientModule.`with`(classOf[TestQualifier])
        module must not be null
      }
    }

    it("binds a lifecycle hook that closes the client") {
      val injector = getTestBindingInjector(List())
      val client = injector
          .getInstance(
            Key.get(classOf[BeadledomClient], classOf[TestBindingAnnotation]))
      val lifecycleHook = injector.getInstance(
        Key.get(classOf[BeadledomClientLifecycleHook], classOf[TestBindingAnnotation]))

      client.isClosed mustBe false
      lifecycleHook.preDestroy()
      client.isClosed mustBe true
    }

    it("provides a BeadledomClient for the specified annotation") {
      val injector = getTestBindingInjector(List())
      val client = injector
          .getInstance(
            Key.get(classOf[BeadledomClient], classOf[TestBindingAnnotation]))

      client must not be null
    }

    it("provides a BeadledomClient for the specified annotation with BeadledomClientConfiguration")
    {
      val correlationIdName = "bd-correlation-id"

      val testModule = new AbstractModule {
        override def configure(): Unit = {
          val config: BeadledomClientConfiguration = BeadledomClientConfiguration
              .builder()
              .connectionPoolSize(1)
              .connectionTimeoutMillis(2)
              .socketTimeoutMillis(2)
              .ttlMillis(2)
              .correlationIdName(correlationIdName)
              .build()

          bind(classOf[BeadledomClientConfiguration])
              .annotatedWith(classOf[TestBindingAnnotation])
              .toInstance(config)
        }
      }

      val injector = getTestBindingInjector(List(testModule))

      val clientBuilder = injector
          .getInstance(Key.get(classOf[BeadledomClientBuilder], classOf[TestBindingAnnotation]))

      val config = clientBuilder.getBeadledomClientConfiguration

      config.connectionPoolSize mustBe 1
      config.connectionTimeoutMillis mustBe 2
      config.socketTimeoutMillis mustBe 2
      config.ttlMillis() mustBe 2
      config.correlationIdName mustBe correlationIdName

      classOf[BeadledomClient].isAssignableFrom(clientBuilder.build.getClass) mustBe true
    }

    it("fallsback to the default correlation Id if it is not set in beadledomClientConfiguration") {
      val testModule = new AbstractModule {
        override def configure(): Unit = {
          val config: BeadledomClientConfiguration = BeadledomClientConfiguration
              .builder()
              .build()

          bind(classOf[BeadledomClientConfiguration])
              .annotatedWith(classOf[TestBindingAnnotation])
              .toInstance(config)
        }
      }

      val injector = getTestBindingInjector(List(testModule))

      val clientBuilder = injector
          .getInstance(Key.get(classOf[BeadledomClientBuilder], classOf[TestBindingAnnotation]))

      val config = clientBuilder.getBeadledomClientConfiguration

      config.correlationIdName mustBe CorrelationIdContext.DEFAULT_HEADER_NAME
    }

    it("binds BeadledomClientBuilderProvider and injects the child Injector") {
      @ext.Provider
      class TestProvider {}

      val testModule = new PrivateModule {
        override def configure(): Unit = {
          install(BeadledomClientModule.`with`(classOf[TestBindingAnnotation]))
          bind(classOf[TestProvider])
              .annotatedWith(classOf[TestBindingAnnotation])
              .toInstance(new TestProvider)

          expose(classOf[BeadledomClientBuilder]).annotatedWith(classOf[TestBindingAnnotation])
        }
      }

      val injector = Guice.createInjector(testModule)
      val clientBuilder = injector
          .getInstance(Key.get(classOf[BeadledomClientBuilder], classOf[TestBindingAnnotation]))


      clientBuilder.getConfiguration.isRegistered(classOf[TestProvider]) mustBe true
    }
  }
}
