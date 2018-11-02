package com.cerner.beadledom.health.resource

import java.net.URI
import java.util.Optional

import com.cerner.beadledom.health.api.DependenciesResource
import com.cerner.beadledom.health.{HealthDependency, HealthStatus}
import com.github.mustachejava.DefaultMustacheFactory
import javax.ws.rs.core.{UriBuilder, UriInfo}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

/**
  * Base Spec for resource tests that provides a create dependency method
  * for creating Health Dependencies.
  *
  * @author Nick Behrens
  */
class BaseSpec extends FunSpec with MustMatchers with MockitoSugar {

  protected val mockUriInfo: UriInfo = mock[UriInfo]
  private val mockUriBuilder = mock[UriBuilder]
  private val url = URI.create("demoUrl")

  when(mockUriInfo.getBaseUriBuilder).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[Class[DependenciesResource]])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.path(any[String])).thenReturn(mockUriBuilder)
  when(mockUriBuilder.build()).thenReturn(url)

  val mustacheFactory = new DefaultMustacheFactory("com/cerner/beadledom/health")

  protected val primaryHealthyDependency: HealthDependency = createDependency("primaryHealthy", healthy = true, primary = true)
  protected val ancillaryHealthyDependency: HealthDependency = createDependency("ancillaryHealthy", healthy = true, primary = false)
  protected val primaryUnhealthyDependency: HealthDependency = createDependency("primaryUnhealthy", healthy = false, primary = false)
  protected val ancillaryUnhealthyDependency: HealthDependency = createDependency("ancillaryUnhealthy", healthy = false, primary = false)


  def createDependency(name: String, healthy: Boolean, primary: Boolean): HealthDependency =
    new HealthDependency {
      override def checkAvailability(): HealthStatus = {
        if (healthy) {
          return HealthStatus.create(200, "ok")
        }
        HealthStatus.create(503, "unavailable")
      }

      override def getName: String = name

      override def getPrimary: java.lang.Boolean = primary

      override def getDescription: Optional[String] = Optional.ofNullable(name)
    }

}
