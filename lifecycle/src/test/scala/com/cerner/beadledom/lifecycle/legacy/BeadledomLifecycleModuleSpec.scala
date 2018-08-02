package com.cerner.beadledom.lifecycle.legacy

import com.google.inject.{AbstractModule, Guice}
import javax.annotation.PostConstruct
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

/**
  * Unit tests for [[BeadledomLifecycleModule]].
  *
  * @author John Leacox
  */
class BeadledomLifecycleModuleSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("BeadledomLifecycleModule") {
    it("provides a binding for LifecycleProvisionListener") {
      val shutdownManager = mock[LifecycleShutdownManager]

      val injector = Guice.createInjector(
        new BeadledomLifecycleModule,
        new AbstractModule() {
          protected override def configure() {
            bind(classOf[LifecycleShutdownManager]).toInstance(shutdownManager)
          }
        })

      injector.getInstance(classOf[LifecycleProvisionListener]) must not be null
    }

    it("binds LifecycleProvisionListener for provisioning events") {
      val shutdownManager = mock[LifecycleShutdownManager]

      val injector = Guice.createInjector(
        new BeadledomLifecycleModule,
        new AbstractModule() {
          protected override def configure() {
            bind(classOf[LifecycleShutdownManager]).toInstance(shutdownManager)
          }
        })

      // Use a PostConstruct method executed by the provision listener to show the listener is
      // registered
      injector.getInstance(classOf[ProvisionedStartupHook]).hasExecutedStartup mustBe true
    }
  }
}

class ProvisionedStartupHook {
  var hasExecutedStartup = false

  @PostConstruct
  def startup(): Unit = {
    hasExecutedStartup = true
  }
}
