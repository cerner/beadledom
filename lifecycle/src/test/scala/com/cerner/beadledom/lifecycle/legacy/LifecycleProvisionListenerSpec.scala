package com.cerner.beadledom.lifecycle.legacy

import com.google.inject.spi.ProvisionListener.ProvisionInvocation
import com.google.inject.{Binding, Key}
import javax.annotation.{PostConstruct, PreDestroy}
import org.hamcrest.Matchers.contains
import org.mockito.Mockito
import org.mockito.hamcrest.MockitoHamcrest
import scala.reflect.Manifest
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Unit tests for [[LifecycleProvisionListener]].
 *
 * @author John Leacox
 */
class LifecycleProvisionListenerSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("LifecycleProvisionListener") {
    describe("#onProvision") {
      it("executes PostConstruct methods on the injectee") {
        val shutdownManager = mock[LifecycleShutdownManager]
        val provisionListener = new LifecycleProvisionListener()
        LifecycleProvisionListener.init(shutdownManager, provisionListener)

        val injectee = new TestPostConstruct
        val provision = createMockProvision(injectee, classOf[TestPostConstruct])
        provisionListener.onProvision(provision)

        injectee.hasExecutedStartup mustBe true
      }

      it("executes PostConstruct methods on the parent class of injectee") {
        val shutdownManager = mock[LifecycleShutdownManager]
        val provisionListener = new LifecycleProvisionListener()
        LifecycleProvisionListener.init(shutdownManager, provisionListener)

        val injectee = new TestPostConstructWithParent
        val provision = createMockProvision(injectee, classOf[TestPostConstructWithParent])
        provisionListener.onProvision(provision)

        injectee.hasExecutedStartup mustBe true
      }

      it("adds PreDestroy methods to the shutdown manager") {
        val shutdownManager = mock[LifecycleShutdownManager]
        val provisionListener = new LifecycleProvisionListener()
        LifecycleProvisionListener.init(shutdownManager, provisionListener)

        val injectee = new TestPreDestroy
        val provision = createMockProvision(injectee, classOf[TestPreDestroy])
        provisionListener.onProvision(provision)

        val invokableShutdownMethod = new InvokableLifecycleMethodImpl(injectee,
          classOf[TestPreDestroy].getDeclaredMethod("shutdown"), classOf[PreDestroy])

        Mockito.verify(shutdownManager).addPreDestroyMethods(
          MockitoHamcrest.argThat(contains(invokableShutdownMethod))
              .asInstanceOf[java.util.List[InvokableLifecycleMethod]])
      }
    }
  }

  def createMockProvision[T](injectee: T, clazz: Class[T])
      (implicit manifest: Manifest[T]): ProvisionInvocation[T] = {
    val binding = mock[Binding[T]]
    val key = Key.get(clazz)
    Mockito.when(binding.getKey).thenReturn(key)

    val invocation = mock[ProvisionInvocation[T]]

    Mockito.when(invocation.getBinding).thenReturn(binding)
    Mockito.when(invocation.provision).thenReturn(injectee)

    invocation
  }

  class TestPostConstruct {
    var hasExecutedStartup: Boolean = false

    @PostConstruct
    def startup(): Unit = {
      hasExecutedStartup = true
    }
  }

  class TestPostConstructWithParent extends TestPostConstruct {
  }

  class TestPreDestroy {
    @PreDestroy
    def shutdown() = ???
  }

}
