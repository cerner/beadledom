package com.cerner.beadledom.lifecycle

import com.cerner.beadledom.lifecycle.legacy.BeadledomLifecycleInjectorBuilder
import com.google.inject.{AbstractModule, Module}
import java.util.Collections
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[LifecycleInjectorBuilder]].
  *
  * @author John Leacox
  */
class LifecycleInjectorBuilderSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("Beadledom LifecycleInjectorBuilder") {
    describe("#fromModules") {
      it("Creates an instance of BeadledomLifecycleInjectorBuilder if governator is not found") {
        val builder = LifecycleInjectorBuilder.fromModules(Collections.emptyList())
        builder mustBe an[BeadledomLifecycleInjectorBuilder]
      }

      it("Creates a builder with the provided modules") {
        val module: Module = new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[String]).toInstance("Hello World")
          }
        }
        val modules = List(module).asJava

        val builder = LifecycleInjectorBuilder.fromModules(modules)
        val injector = builder.createInjector()

        injector.getInstance(classOf[String]) mustBe "Hello World"
      }
    }
  }
}
