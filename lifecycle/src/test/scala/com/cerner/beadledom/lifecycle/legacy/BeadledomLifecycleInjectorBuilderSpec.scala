package com.cerner.beadledom.lifecycle.legacy

import com.cerner.beadledom.lifecycle.legacy.ShutdownHookModule.SystemShutdownHook
import com.google.inject.{AbstractModule, Module}
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[BeadledomLifecycleInjectorBuilder]].
  *
  * @author John Leacox
  */
class BeadledomLifecycleInjectorBuilderSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("BeadledomLifecycleInjectorBuilder") {
    describe("#modules") {
      it("throws a NullPointerException if the modules param is null") {
        val builder = new BeadledomLifecycleInjectorBuilder

        intercept[NullPointerException] {
          builder.modules(null)
        }
      }
    }

    describe("#createInjector") {
      it("creates an injector with a binding for LifecycleProvisionListener") {
        val injector = new BeadledomLifecycleInjectorBuilder().createInjector()
        injector.getInstance(classOf[LifecycleProvisionListener]) must not be null
      }

      it("creates an injector with a binding for LifecycleShutdownManager") {
        val injector = new BeadledomLifecycleInjectorBuilder().createInjector()
        injector.getInstance(classOf[LifecycleShutdownManager]) must not be null
      }

      it("creates an injector with a binding for SystemShutdownHook") {
        val injector = new BeadledomLifecycleInjectorBuilder().createInjector()
        injector.getInstance(classOf[SystemShutdownHook]) must not be null
      }

      it("creates an injector including user specified modules") {
        val builder = new BeadledomLifecycleInjectorBuilder
        val module: Module = new AbstractModule() {
          override def configure(): Unit = {
            bind(classOf[String]).toInstance("Hello World")
          }
        }

        builder.modules(List(module).asJava)

        val injector = builder.createInjector()
        injector.getInstance(classOf[String]) mustBe "Hello World"
      }
    }
  }
}
