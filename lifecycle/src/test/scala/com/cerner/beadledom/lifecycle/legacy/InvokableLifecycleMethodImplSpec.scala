package com.cerner.beadledom.lifecycle.legacy

import java.lang.reflect.Method
import javax.annotation.PreDestroy
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[InvokableLifecycleMethodImpl]].
  *
  * @author John Leacox
  */
class InvokableLifecycleMethodImplSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("InvokableLifecycleMethodImpl") {
    describe("#invoke") {
      it("invokes the wrapped lifecycle method") {
        val lifecycleHook = new TestLifecycleHook
        val shutdownMethod = findPreDestroyMethod(lifecycleHook, classOf[TestLifecycleHook])
        val lifecycleMethod = new InvokableLifecycleMethodImpl(lifecycleHook, shutdownMethod,
          classOf[PreDestroy])
        lifecycleMethod.invoke()

        lifecycleHook.hasExecutedShutdown mustBe true
      }

      it("invokes the wrapped lifecycle method when the method is private") {
        val lifecycleHook = new TestPrivateLifecycleHook
        val shutdownMethod = findPreDestroyMethod(lifecycleHook, classOf[TestPrivateLifecycleHook])
        val lifecycleMethod = new InvokableLifecycleMethodImpl(lifecycleHook, shutdownMethod,
          classOf[PreDestroy])
        lifecycleMethod.invoke()

        lifecycleHook.hasExecutedShutdown mustBe true
      }
    }
  }

  def findPreDestroyMethod(obj: Object, clazz: Class[_]): Method = {
    getAllMethods(clazz).filter { method =>
      method.isAnnotationPresent(classOf[PreDestroy])
    }.head
  }

  private def getAllMethods(`type`: Class[_]): List[Method] = {
    val allMethods: mutable.Buffer[Method] = new ListBuffer[Method]
    var clazz: Class[_] = `type`
    while (clazz != null) {
      allMethods ++= clazz.getDeclaredMethods
      clazz = clazz.getSuperclass
    }

    allMethods.toList
  }
}
