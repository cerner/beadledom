package com.cerner.beadledom.lifecycle.governator

import com.google.inject.{AbstractModule, Module}
import com.netflix.governator.InjectorBuilder
import javax.annotation.PreDestroy
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}
import scala.collection.JavaConverters._

/**
  * Unit tests for [[GovernatorLifecycleInjector]]
  *
  * @author John Leacox
  */
class GovernatorLifecycleInjectorSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("GovernatorLifecycleInjector") {
    describe("#shutdown") {
      it("executes shutdown on the governator injector") {
        val module: Module = new AbstractModule {
          override def configure(): Unit = {
            bind(classOf[GovernatorShutdownHook]).asEagerSingleton()
          }
        }
        val injector = InjectorBuilder.fromModules(List(module).asJava).createInjector
        val lifecycleInjector = GovernatorLifecycleInjector.create(injector)

        lifecycleInjector.shutdown()

        injector.getInstance(classOf[GovernatorShutdownHook]).shutdownHasExecuted mustBe true
      }
    }
  }
}

class GovernatorShutdownHook {
  var shutdownHasExecuted = false

  @PreDestroy
  def shutdown() = {
    shutdownHasExecuted = true
  }
}
