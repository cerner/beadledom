package com.cerner.beadledom.health.resource

import com.cerner.beadledom.health.dto.HealthDependencyDto
import com.cerner.beadledom.health.internal.HealthChecker
import com.cerner.beadledom.health.{HealthDependency, HealthServiceModule}
import com.google.inject.multibindings.Multibinder
import com.google.inject.{AbstractModule, Guice}
import javax.ws.rs.core.UriInfo

/**
  * Spec tests for DependenciesResourceImplSpec.
  *
  * @author Nimesh Subramanian
  */
class DependenciesResourceImplSpec extends BaseSpec {

  private val dependencyModule = new AbstractModule {
    override def configure(): Unit = {
      val multibinder: Multibinder[HealthDependency] =
        Multibinder.newSetBinder(binder(), classOf[HealthDependency])
      multibinder.addBinding().toInstance(primaryHealthyDependency)
      multibinder.addBinding().toInstance(ancillaryHealthyDependency)
      install(new HealthServiceModule)
      bind(classOf[UriInfo]).toInstance(mockUriInfo)
    }
  }
  private val injector = Guice.createInjector(dependencyModule)
  private val checker = injector.getInstance(classOf[HealthChecker])

  val dependencyResource = new DependenciesResourceImpl(checker, mustacheFactory)


  describe("DependenciesResourceImpl") {
    describe("#getDependencyListing") {
      it("returns a list of all dependencies") {
        val dependencyList = dependencyResource.getDependencyListing

        dependencyList.size() must be(2)
      }
    }

    describe("#getDependencyAvailabilityCheck") {
      it("returns a dto with status 200") {
        val healthCheck = dependencyResource.getDependencyAvailabilityCheck(primaryHealthyDependency.getName)
        val healthDependencyDto = healthCheck.getEntity.asInstanceOf[HealthDependencyDto]

        healthCheck.getStatus must be(200)
        healthDependencyDto.getName.get() must be(primaryHealthyDependency.getName)
      }
    }
  }
}
