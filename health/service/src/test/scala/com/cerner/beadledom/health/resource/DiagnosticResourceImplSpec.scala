package com.cerner.beadledom.health.resource

import com.cerner.beadledom.health.api.DependenciesResource
import com.cerner.beadledom.health.dto.HealthDto
import com.cerner.beadledom.health.internal.HealthChecker
import com.cerner.beadledom.health.{TestModule1, TestModule2}
import com.github.mustachejava.DefaultMustacheFactory
import com.google.inject.{AbstractModule, Guice}
import java.net.URI
import javax.ws.rs.core.{UriBuilder, UriInfo}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest._
import org.scalatest.mock.MockitoSugar

/**
 * Spec tests for DiagnosticResourceImpl.
 *
 * @author Nimesh Subramanian
 */
class DiagnosticResourceImplSpec extends FunSpec with MustMatchers with MockitoSugar {
  val mockUriInfo = mock[UriInfo]
  val mockUriBuilder = mock[UriBuilder]
  val url = URI.create("demoUrl")
  val mustacheFactory = new DefaultMustacheFactory("com/cerner/beadledom/health")

  when(mockUriInfo.getBaseUriBuilder()).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[Class[DependenciesResource]])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[String])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.build()).thenReturn(url)

  describe("DiagnosticResourceImpl") {
    describe("#getDiagnosticHealthCheck") {
      it("returns a dto with status 200") {
        val healthModule = new AbstractModule {
          override def configure(): Unit = {
            install(new TestModule1)
            bind(classOf[UriInfo]).toInstance(mockUriInfo)
          }
        }

        val injector = Guice.createInjector(healthModule)
        val checker = injector.getInstance(classOf[HealthChecker])
        val healthResource = new DiagnosticResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getDiagnosticHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be ("Lombre is available")
        healthDto.getStatus must be (200)
        healthDto.getDependencies.get().size() must be (2)
        healthDto.getDependencies.get().get(0).getName.get() must be ("HealthDependency1")
        healthDto.getDependencies.get().get(1).getName.get() must be ("HealthDependency2")
      }

      it("returns a dto with status 503") {
        val healthModule = new AbstractModule {
          override def configure(): Unit = {
            install(new TestModule2)
            bind(classOf[UriInfo]).toInstance(mockUriInfo)
          }
        }

        val injector = Guice.createInjector(healthModule)
        val checker = injector.getInstance(classOf[HealthChecker])
        val healthResource = new DiagnosticResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getDiagnosticHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be("Lombre is unavailable")
        healthDto.getStatus must be(503)
        healthDto.getDependencies.get().size() must be(3)
        healthDto.getDependencies.get().get(0).getName.get() must be ("HealthDependency1")
        healthDto.getDependencies.get().get(1).getName.get() must be ("HealthDependency2")
        healthDto.getDependencies.get().get(2).getName.get() must be ("HealthDependency3")
      }
    }
  }
}
