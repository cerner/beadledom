package com.cerner.beadledom.lifecycle.legacy

import com.google.inject.Injector
import org.mockito.Mockito
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[BeadledomLifecycleInjector]].
  *
  * @author John Leacox
  */
class BeadledomLifecycleInjectorSpec extends AnyFunSpec with Matchers with MockitoSugar {
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
