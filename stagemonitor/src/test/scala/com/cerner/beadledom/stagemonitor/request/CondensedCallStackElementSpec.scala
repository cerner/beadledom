package com.cerner.beadledom.stagemonitor.request

import org.scalatest._
import org.stagemonitor.requestmonitor.profiler.CallStackElement

/**
  * Spec tests for {@link CondensedCallStackElement}.
  *
  * @author John Leacox
  */
class CondensedCallStackElementSpec extends FunSpec with MustMatchers {
  describe("CondensedCallStackElement") {
    describe("#of") {
      it("condenses child elements") {
        val signature = "Object com.cerner.beadledom.health." +
            "resource.AvailabilityResource..FastClassByGuice..77b09000.newInstance(int, Object[])"
        val parent = CallStackElement.create(null, signature, 0l)

        val childSignature =
          "void org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher." +
              "service(HttpServletRequest, HttpServletResponse)"
        val child = CallStackElement.create(parent, childSignature, 0l)

        val condensedCallStack = CondensedCallStackElement.of(parent)

        condensedCallStack.getSignature mustBe
            "Object c.c.b.h.r.A.F.77b09000.newInstance(int, Object[])"
        condensedCallStack.getChildren.get(0).getSignature mustBe
            "void o.j.r.p.s.s.HttpServletDispatcher.service(HttpServletRequest, HttpServletResponse)"
      }
    }

    describe("#getSignature") {
      it("condenses the package name for a class") {
        val signature = "void org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher." +
            "service(HttpServletRequest, HttpServletResponse)"
        val callStackElement = CondensedCallStackElement
            .of(CallStackElement.create(null, signature, 0l))

        val condensedSig = callStackElement.getSignature
        condensedSig must be(
          "void o.j.r.p.s.s.HttpServletDispatcher.service(HttpServletRequest, HttpServletResponse)")
      }

      it("condenses the package name for a guice generated class") {
        val signature = "Object com.cerner.beadledom.health." +
            "resource.AvailabilityResource..FastClassByGuice..77b09000.newInstance(int, Object[])"
        val callStackElement = CondensedCallStackElement
            .of(CallStackElement.create(null, signature, 0l))

        val condensedSig = callStackElement.getSignature

        condensedSig must be("Object c.c.b.h.r.A.F.77b09000.newInstance(int, Object[])")
      }
    }
  }
}
