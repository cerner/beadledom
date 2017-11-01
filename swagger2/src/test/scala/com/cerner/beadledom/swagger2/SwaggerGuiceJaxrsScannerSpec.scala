package com.cerner.beadledom.swagger2

import com.google.inject.{Binding, Injector, Key}
import io.swagger.models.Info
import javax.servlet.ServletConfig
import javax.ws.rs.core.Application
import org.mockito.Mockito.{reset, when}
import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import scala.collection.JavaConverters._

/**
 * Spec tests for [SwaggerGuiceJaxrsScanner].
 *
 * @author John Leacox
 */
class SwaggerGuiceJaxrsScannerSpec
    extends FunSpec with BeforeAndAfter with MustMatchers with MockitoSugar {
  val swaggerInfo: Info = mock[Info]

  before {
    reset(swaggerInfo)
  }

  describe("SwaggerGuiceJaxrsScanner") {
    describe("#classesFromContext") {
      it("returns classes with direct path annotation") {
        val parentKey = Key.get(classOf[ParentResource])
        val parentBinding = mock[Binding[ParentResource]]
        when(parentBinding.getKey).thenReturn(parentKey)

        val bindings = Map(parentKey -> parentBinding).asInstanceOf[Map[Key[_], Binding[_]]].asJava

        val injector = mock[Injector]
        when(injector.getBindings).thenReturn(bindings)

        val app = mock[Application]
        val servletConfig = mock[ServletConfig]

        val jaxrsScanner = new SwaggerGuiceJaxrsScanner(injector, swaggerInfo)
        val classes = jaxrsScanner.classesFromContext(app, servletConfig)

        classes must have size 1
        classes must contain(classOf[ParentResource])
      }

      it("returns classes with parent class path annotation") {
        val key = Key.get(classOf[ChildResource])
        val binding = mock[Binding[ChildResource]]
        when(binding.getKey).thenReturn(key)

        val bindings = Map(key -> binding).asInstanceOf[Map[Key[_], Binding[_]]].asJava

        val injector = mock[Injector]
        when(injector.getBindings).thenReturn(bindings)

        val app = mock[Application]
        val servletConfig = mock[ServletConfig]

        val jaxrsScanner = new SwaggerGuiceJaxrsScanner(injector, swaggerInfo)
        val classes = jaxrsScanner.classesFromContext(app, servletConfig)

        classes must have size 1
        classes must contain(classOf[ChildResource])
      }

      it("returns classes with grand parent class path annotation") {
        val key = Key.get(classOf[GrandchildResource])
        val binding = mock[Binding[GrandchildResource]]
        when(binding.getKey).thenReturn(key)

        val bindings = Map(key -> binding).asInstanceOf[Map[Key[_], Binding[_]]].asJava

        val injector = mock[Injector]
        when(injector.getBindings).thenReturn(bindings)

        val app = mock[Application]
        val servletConfig = mock[ServletConfig]

        val jaxrsScanner = new SwaggerGuiceJaxrsScanner(injector, swaggerInfo)
        val classes = jaxrsScanner.classesFromContext(app, servletConfig)

        classes must have size 1
        classes must contain(classOf[GrandchildResource])
      }

      it("returns classes with parent interface path annotation") {
        val key = Key.get(classOf[ImplementationResource])
        val binding = mock[Binding[ImplementationResource]]
        when(binding.getKey).thenReturn(key)

        val bindings = Map(key -> binding).asInstanceOf[Map[Key[_], Binding[_]]].asJava

        val injector = mock[Injector]
        when(injector.getBindings).thenReturn(bindings)

        val app = mock[Application]
        val servletConfig = mock[ServletConfig]

        val jaxrsScanner = new SwaggerGuiceJaxrsScanner(injector, swaggerInfo)
        val classes = jaxrsScanner.classesFromContext(app, servletConfig)

        classes must have size 1
        classes must contain(classOf[ImplementationResource])
      }

      it("returns classes with grand parent interface path annotation") {
        val key = Key.get(classOf[ChildImplementationResource])
        val binding = mock[Binding[ChildImplementationResource]]
        when(binding.getKey).thenReturn(key)

        val bindings = Map(key -> binding).asInstanceOf[Map[Key[_], Binding[_]]].asJava

        val injector = mock[Injector]
        when(injector.getBindings).thenReturn(bindings)

        val app = mock[Application]
        val servletConfig = mock[ServletConfig]

        val jaxrsScanner = new SwaggerGuiceJaxrsScanner(injector, swaggerInfo)
        val classes = jaxrsScanner.classesFromContext(app, servletConfig)

        classes must have size 1
        classes must contain(classOf[ChildImplementationResource])
      }

      it("does not return classes with with no path annotation") {
        val key = Key.get(classOf[NonResource])
        val binding = mock[Binding[NonResource]]
        when(binding.getKey).thenReturn(key)

        val bindings = Map(key -> binding).asInstanceOf[Map[Key[_], Binding[_]]].asJava

        val injector = mock[Injector]
        when(injector.getBindings).thenReturn(bindings)

        val app = mock[Application]
        val servletConfig = mock[ServletConfig]

        val jaxrsScanner = new SwaggerGuiceJaxrsScanner(injector, swaggerInfo)
        val classes = jaxrsScanner.classesFromContext(app, servletConfig)

        classes must have size 0
      }
    }
  }
}
