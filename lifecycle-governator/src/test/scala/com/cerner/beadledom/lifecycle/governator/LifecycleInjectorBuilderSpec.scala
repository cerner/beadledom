package com.cerner.beadledom.lifecycle.governator

import com.cerner.beadledom.lifecycle.LifecycleInjectorBuilder
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
  describe("Governator LifecycleInjectorBuilder") {
    describe("#fromModules") {
      it("defaults to the governator implementation, if found on classpath") {
        val builder = LifecycleInjectorBuilder.fromModules(Collections.emptyList())
        builder mustBe an[GovernatorLifecycleInjectorBuilder]
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
