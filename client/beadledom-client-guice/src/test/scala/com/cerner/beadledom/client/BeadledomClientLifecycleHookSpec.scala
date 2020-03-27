package com.cerner.beadledom.client

import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
  * Unit tests for ResteasyClientLifecycleHook.
  *
  * @author John Leacox
  */
class BeadledomClientLifecycleHookSpec extends AnyFunSpec with Matchers {
  describe("BeadledomClientLifecycleHook") {
    it("closes the BeadledomClient when @PreDestroy is executed") {
      val client = mock(classOf[BeadledomClient])

      val lifecycleHook = new BeadledomClientLifecycleHook(client, classOf[TestBindingAnnotation])
      lifecycleHook.preDestroy()

      Mockito.verify(client).close()
    }
  }
}
