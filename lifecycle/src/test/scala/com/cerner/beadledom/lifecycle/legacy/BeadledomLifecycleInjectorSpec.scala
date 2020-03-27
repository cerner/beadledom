package com.cerner.beadledom.lifecycle.legacy

import com.google.inject.Injector
import org.mockito.Mockito
import org.scalatest.{FunSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar

/**
  * Unit tests for [[BeadledomLifecycleInjector]].
  *
  * @author John Leacox
  */
class BeadledomLifecycleInjectorSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("BeadledomLifecycleInjector") {
    describe("#shutdown") {
      it("executes shutdown on the shutdown manager") {
        val injector = mock[Injector]
        val shutdownManager = mock[LifecycleShutdownManager]
        val lifecycleInjector = BeadledomLifecycleInjector.create(injector, shutdownManager)

        lifecycleInjector.shutdown()

        Mockito.verify(shutdownManager).shutdown()
      }
    }
  }
}
