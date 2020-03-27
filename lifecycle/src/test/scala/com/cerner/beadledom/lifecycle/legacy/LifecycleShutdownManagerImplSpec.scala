package com.cerner.beadledom.lifecycle.legacy

import org.mockito.Mockito
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[LifecycleShutdownManagerImpl]].
  *
  * @author John Leacox
  */
class LifecycleShutdownManagerImplSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("LifecycleShutdownManagerImpl") {
    describe("#addPreDestroyMethods") {
      it("adds to the full set of predestroy methods when called multiple times") {
        val shutdownManager = new LifecycleShutdownManagerImpl

        val shutdownMethod1 = mock[InvokableLifecycleMethod]
        val shutdownMethod2 = mock[InvokableLifecycleMethod]
        val shutdownMethods1 = List(shutdownMethod1, shutdownMethod2).asJava
        shutdownManager.addPreDestroyMethods(shutdownMethods1)

        val shutdownMethod3 = mock[InvokableLifecycleMethod]
        val shutdownMethod4 = mock[InvokableLifecycleMethod]
        val shutdownMethods2 = List(shutdownMethod3, shutdownMethod4).asJava
        shutdownManager.addPreDestroyMethods(shutdownMethods2)

        shutdownManager.shutdown()

        Mockito.verify(shutdownMethod1).invoke()
        Mockito.verify(shutdownMethod2).invoke()
        Mockito.verify(shutdownMethod3).invoke()
        Mockito.verify(shutdownMethod4).invoke()
      }
    }

    describe("#shutdown") {
      it("executes all registered lifecycle shutdown methods") {
        val shutdownMethod1 = mock[InvokableLifecycleMethod]
        val shutdownMethod2 = mock[InvokableLifecycleMethod]
        val shutdownMethod3 = mock[InvokableLifecycleMethod]

        val shutdownMethods = List(shutdownMethod1, shutdownMethod2, shutdownMethod3).asJava

        val shutdownManager = new LifecycleShutdownManagerImpl
        shutdownManager.addPreDestroyMethods(shutdownMethods)

        shutdownManager.shutdown()

        Mockito.verify(shutdownMethod1).invoke()
        Mockito.verify(shutdownMethod2).invoke()
        Mockito.verify(shutdownMethod3).invoke()
      }

      it("executes all registered lifecycle shutdown methods when an exception occurs") {
        val shutdownMethod1 = mock[InvokableLifecycleMethod]
        Mockito.when(shutdownMethod1.invoke()).thenThrow(new RuntimeException)

        val shutdownMethod2 = mock[InvokableLifecycleMethod]
        val shutdownMethod3 = mock[InvokableLifecycleMethod]

        val shutdownMethods = List(shutdownMethod1, shutdownMethod2, shutdownMethod3).asJava

        val shutdownManager = new LifecycleShutdownManagerImpl
        shutdownManager.addPreDestroyMethods(shutdownMethods)

        shutdownManager.shutdown()

        Mockito.verify(shutdownMethod1).invoke()
        Mockito.verify(shutdownMethod2).invoke()
        Mockito.verify(shutdownMethod3).invoke()
      }
    }
  }
}
