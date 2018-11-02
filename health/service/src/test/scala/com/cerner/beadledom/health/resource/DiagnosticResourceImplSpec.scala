package com.cerner.beadledom.health.resource

import com.cerner.beadledom.health.dto.HealthDto
import com.cerner.beadledom.health.internal.HealthChecker
import com.cerner.beadledom.health.{HealthDependency, HealthServiceModule}
import com.google.inject.multibindings.Multibinder
import com.google.inject.{AbstractModule, Guice}
import javax.ws.rs.core.UriInfo

/**
  * Spec tests for DiagnosticResourceImpl.
  *
  * @author Nimesh Subramanian
  */

class DiagnosticResourceImplSpec extends BaseSpec {

  describe("DiagnosticResourceImpl") {
    describe("#getDiagnosticHealthCheck") {
      it("returns a dto with status 200") {
        val dependencyModule = new AbstractModule {
          override def configure(): Unit = {
            val multibinder: Multibinder[HealthDependency] =
              Multibinder.newSetBinder(binder(), classOf[HealthDependency])
            multibinder.addBinding().toInstance(primaryHealthyDependency)
            multibinder.addBinding().toInstance(ancillaryHealthyDependency)
            install(new HealthServiceModule)
            bind(classOf[UriInfo]).toInstance(mockUriInfo)
          }
        }

        val injector = Guice.createInjector(dependencyModule)
        val checker = injector.getInstance(classOf[HealthChecker])
        val healthResource = new DiagnosticResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getDiagnosticHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be("myService is available")
        healthDto.getStatus must be(200)
        healthDto.getDependencies.get().size() must be(2)
        healthDto.getDependencies.get().get(0).getName.get() must be(ancillaryHealthyDependency.getName)
        healthDto.getDependencies.get().get(1).getName.get() must be(primaryHealthyDependency.getName)
      }

      it("returns a dto with status 503") {
        val dependencyModule = new AbstractModule {
          override def configure(): Unit = {
            val multibinder: Multibinder[HealthDependency] =
              Multibinder.newSetBinder(binder(), classOf[HealthDependency])
            multibinder.addBinding().toInstance(primaryUnhealthyDependency)
            multibinder.addBinding().toInstance(ancillaryUnhealthyDependency)
            install(new HealthServiceModule)
            bind(classOf[UriInfo]).toInstance(mockUriInfo)
          }
        }

        val injector = Guice.createInjector(dependencyModule)
        val checker = injector.getInstance(classOf[HealthChecker])
        val healthResource = new DiagnosticResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getDiagnosticHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be("myService is unavailable")
        healthDto.getStatus must be(503)
        healthDto.getDependencies.get().size() must be(2)
        healthDto.getDependencies.get().get(0).getName.get() must be(ancillaryUnhealthyDependency.getName)
        healthDto.getDependencies.get().get(1).getName.get() must be(primaryUnhealthyDependency.getName)
      }
    }
  }
}
