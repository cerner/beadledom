package com.cerner.beadledom.health.resource

import com.cerner.beadledom.health.dto.HealthDto
import com.cerner.beadledom.health.internal.HealthChecker
import com.cerner.beadledom.health.{HealthDependency, HealthServiceModule}
import com.google.inject.multibindings.Multibinder
import com.google.inject.{AbstractModule, Guice, Injector}
import javax.ws.rs.core.UriInfo

/**
  * Spec tests for HealthResourceImplSpec.
  *
  * @author Nimesh Subramanian
  */

class HealthResourceImplSpec(injector: Injector) extends BaseSpec {

  describe("HealthResourceImpl") {
    describe("#getPrimaryHealthCheck") {
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
        val healthResource = new HealthResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getPrimaryHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be("myService is available")
        healthDto.getStatus must be(200)

        val healthDependencies = healthDto.getDependencies.get()

        healthDependencies.size() must be(1)
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
        val healthResource = new HealthResourceImpl(checker, mustacheFactory)
        val healthCheck = healthResource.getPrimaryHealthCheck
        val healthDto = healthCheck.getEntity.asInstanceOf[HealthDto]

        healthDto.getMessage.get() must be("artifactId is unavailable")
        healthDto.getStatus must be(503)

        val healthDependencies = healthDto.getDependencies.get()

        healthDependencies.size() must be(1)
      }
    }
  }
}
