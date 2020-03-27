package com.cerner.beadledom.lifecycle

import com.google.inject.{AbstractModule, Inject, Module}
import java.util.Collections
import javax.annotation.{PostConstruct, PreDestroy}
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[GuiceLifecycleContainers]].
  *
  * @author John Leacox
  */
class GuiceLifecycleContainersSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("GuiceLifecycleContainers") {
    describe("#initialize") {
      it("throws a NullPointerException for a null container") {
        val modules = Collections.emptyList[Module]
        intercept[NullPointerException] {
          GuiceLifecycleContainers.initialize(null.asInstanceOf[LifecycleContainer], modules)
        }
      }

      it("throws a NullPointerException for a null modules list") {
        val container = new LifecycleContainer {}
        intercept[NullPointerException] {
          GuiceLifecycleContainers.initialize(container, null)
        }
      }

      it("creates an injector with the provided modules") {
        val module: Module = new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[String]).toInstance("Hello World")
          }
        }
        val modules = List(module).asJava

        val container = new LifecycleContainer {}

        val lifecycleInjector = GuiceLifecycleContainers.initialize(container, modules)

        lifecycleInjector.getInstance(classOf[String]) mustBe "Hello World"
      }

      it("injects the container members") {
        val container = new TestContainer

        val module: Module = new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[String]).toInstance("Hello World")
            bind(classOf[Boolean]).toInstance(true)
          }
        }
        val modules = List(module).asJava

        container.fieldInjection mustBe null.asInstanceOf[String]
        container.methodInjection mustBe false

        val lifecycleInjector = GuiceLifecycleContainers.initialize(container, modules)

        container.fieldInjection mustBe "Hello World"
        container.methodInjection mustBe true
      }

      it("executes PostConstruct lifecycle methods in the registered modules") {
        val module: Module = new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[TestLifecycleHook]).asEagerSingleton()
          }
        }
        val modules = List(module).asJava
        val container = new LifecycleContainer {}

        val lifecycleInjector = GuiceLifecycleContainers.initialize(container, modules)

        val hook = lifecycleInjector.getInstance(classOf[TestLifecycleHook])
        hook.hasExecutedStartup mustBe true
        hook.hasExecutedShutdown mustBe false
      }

      it(
        "returns a LifecycleInjector that executes PreDestroy lifecycle methods in the " +
            "registered modules on shutdown") {
        val module: Module = new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[TestLifecycleHook]).asEagerSingleton()
          }
        }

        val modules = List(module).asJava
        val container = new LifecycleContainer {}

        val lifecycleInjector = GuiceLifecycleContainers.initialize(container, modules)

        val hook = lifecycleInjector.getInstance(classOf[TestLifecycleHook])

        lifecycleInjector.shutdown()
        hook.hasExecutedShutdown mustBe true
      }
    }
  }
}

class TestContainer extends LifecycleContainer {
  var methodInjection: Boolean = _

  @Inject
  var fieldInjection: String = _

  @Inject
  def init(value: Boolean) = {
    methodInjection = value
  }
}

class TestLifecycleHook {
  var hasExecutedStartup = false
  var hasExecutedShutdown = false

  @PostConstruct
  def startup(): Unit = {
    hasExecutedStartup = true
  }

  @PreDestroy
  def shutdown(): Unit = {
    hasExecutedShutdown = true
  }
}
