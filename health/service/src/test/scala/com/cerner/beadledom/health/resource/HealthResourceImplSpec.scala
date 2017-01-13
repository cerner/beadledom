package com.cerner.beadledom.health.resource

import com.cerner.beadledom.health.api.DependenciesResource
import com.cerner.beadledom.health.dto.HealthDto
import com.cerner.beadledom.health.internal.HealthChecker
import com.cerner.beadledom.health.{TestModule1, TestModule2}
import com.github.mustachejava.DefaultMustacheFactory
import com.google.inject.{AbstractModule, Guice}
import java.net.URI
import javax.ws.rs.core.{UriBuilder, UriInfo}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest._
import org.scalatest.mock.MockitoSugar

/**
 * Spec tests for HealthResourceImplSpec.
 *
 * @author Nimesh Subramanian
 */
class HealthResourceImplSpec extends FunSpec with MustMatchers with MockitoSugar {
  val mockUriInfo = mock[UriInfo]
  val mockUriBuilder = mock[UriBuilder]
  val url = URI.create("demoUrl")

  when(mockUriInfo.getBaseUriBuilder()).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[Class[DependenciesResource]])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[String])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.build()).thenReturn(url)

  describe("HealthResourceImpl") {
    describe("#getPrimaryHealthCheck") {
      it("returns a dto with status 200") {
        val mustacheFactory = new DefaultMustacheFactory("com/cerner/beadledom/health")
        val healthModule = new AbstractModule {
          override def configure(): Unit = {
            install(new TestModule1)
            bind(classOf[UriInfo]).toInstance(mockUriInfo)
          }
        }

        val injector = Guice.createInjector(healthModule)
        val checker = injector.getInstance(classOf[HealthChecker])
        val healthResource = new HealthResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getPrimaryHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be ("Lombre is available")
        healthDto.getStatus must be (200)

        val healthDependencies = healthDto.getDependencies.get()

        healthDependencies.size() must be (2)
      }

      it("returns a dto with status 503") {
        val mustacheFactory = new DefaultMustacheFactory("com/cerner/beadledom/health")
        val healthModule = new AbstractModule {
          override def configure(): Unit = {
            install(new TestModule2)
            bind(classOf[UriInfo]).toInstance(mockUriInfo)
          }
        }

        val injector = Guice.createInjector(healthModule)
        val checker = injector.getInstance(classOf[HealthChecker])
        val healthResource = new HealthResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getPrimaryHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be ("Lombre is unavailable")
        healthDto.getStatus must be (503)

        val healthDependencies = healthDto.getDependencies.get()

        healthDependencies.size() must be (3)
      }
    }
  }
}
