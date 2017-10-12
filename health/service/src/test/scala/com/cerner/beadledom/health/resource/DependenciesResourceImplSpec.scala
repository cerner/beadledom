package com.cerner.beadledom.health.resource

import com.cerner.beadledom.health.TestModule1
import com.cerner.beadledom.health.api.DependenciesResource
import com.cerner.beadledom.health.dto.HealthDependencyDto
import com.cerner.beadledom.health.internal.HealthChecker
import com.github.mustachejava.DefaultMustacheFactory
import com.google.inject.{AbstractModule, Guice}
import java.net.URI
import javax.ws.rs.core.{UriBuilder, UriInfo}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest._
import org.scalatest.mock.MockitoSugar

/**
 * Spec tests for DependenciesResourceImplSpec.
 *
 * @author Nimesh Subramanian
 */
class DependenciesResourceImplSpec extends FunSpec with MustMatchers with MockitoSugar {
  val mustacheFactory = new DefaultMustacheFactory("com/cerner/beadledom/health")
  val mockUriInfo = mock[UriInfo]
  val mockUriBuilder = mock[UriBuilder]
  val url = URI.create("demoUrl")
  val healthModule = new AbstractModule {
    override def configure(): Unit = {
      install(new TestModule1)
      bind(classOf[UriInfo]).toInstance(mockUriInfo)
    }
  }
  val injector = Guice.createInjector(healthModule)
  val checker = injector.getInstance(classOf[HealthChecker])

  when(mockUriInfo.getBaseUriBuilder()).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[Class[DependenciesResource]])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[String])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.build()).thenReturn(url)

  describe("DependenciesResourceImpl") {
    describe("#getDependencyListing") {
      it("returns a dependencies") {
        val healthResource = new DependenciesResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getDependencyListing

        healthCheck.size() must be (2)
      }
    }

    describe("#getDependencyAvailabilityCheck") {
      it("returns a dto with status 200") {
        val dependencyResource = new DependenciesResourceImpl(checker, mustacheFactory)
        val healthCheck = dependencyResource.getDependencyAvailabilityCheck("HealthDependency2")
        val healthDependencyDto = healthCheck.getEntity.asInstanceOf[HealthDependencyDto]

        healthDependencyDto.getName.get() must be ("HealthDependency2")
      }
    }
  }
}
