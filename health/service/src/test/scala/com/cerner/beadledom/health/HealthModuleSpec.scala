package com.cerner.beadledom.health

import com.cerner.beadledom.metadata.ServiceMetadata
import com.google.inject._
import com.google.inject.multibindings.{Multibinder, ProvidesIntoSet}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}
import javax.ws.rs.core.UriInfo

class HealthModuleSpec extends FunSpec with MustMatchers with MockitoSugar {
  val mockModule = new AbstractModule {
    override def configure(): Unit = {
      bind(classOf[ServiceMetadata]).toInstance(mock[ServiceMetadata])
      bind(classOf[UriInfo]).toInstance(mock[UriInfo])
    }
  }

  describe("HealthModule") {
    describe("#provideHealthDependencyMap") {
      val mapType = new TypeLiteral[java.util.Map[String, HealthDependency]] {}

      val healthDependencyModule = new AbstractModule {
        override def configure(): Unit = {

          val healthDependencyBinder = Multibinder
              .newSetBinder(binder(), classOf[HealthDependency])

          install(mockModule)
          install(new HealthModule)

          healthDependencyBinder.addBinding().to(classOf[Dependency1])
          healthDependencyBinder.addBinding().to(classOf[Dependency2])
        }
      }

      it("detects duplicate names") {
        val injector = Guice.createInjector(new AbstractModule {
          override def configure(): Unit = {
            install(healthDependencyModule)
          }

          @ProvidesIntoSet
          def provideHealthDependency1(): HealthDependency = {
            return new Dependency1
          }
        })
        a[ProvisionException] must be thrownBy injector.getInstance(Key.get(mapType))
      }

      it("creates map of dependencies from set") {
        val injector = Guice.createInjector(healthDependencyModule)

        val dependencies = injector.getInstance(Key.get(mapType))
        dependencies must have size 2
        dependencies.get("alpha") mustBe a[Dependency1]
        dependencies.get("beta") mustBe a[Dependency2]
      }

      describe("when HTTP health dependency is used") {
        val httpHealthDependencyModule: Module = new AbstractModule {
          override def configure(): Unit = {

            val healthDependencyBinder = Multibinder
                .newSetBinder(binder(), classOf[HealthDependency])

            install(mockModule)
            install(new HealthModule)

            healthDependencyBinder.addBinding().to(classOf[Dependency1])
          }

          @Inject
          @Singleton
          @ProvidesIntoSet
          def provideHttpHealthDependency(): HealthDependency = {
            new HttpHealthDependency("availabilityUrl", "gamma", true)
          }
        }

        it("creates map of dependencies from set") {
          val injector = Guice.createInjector(httpHealthDependencyModule)

          val dependencies = injector.getInstance(Key.get(mapType))
          dependencies must have size 2
          dependencies.get("alpha") mustBe a[Dependency1]
          dependencies.get("gamma") mustBe a[HttpHealthDependency]
        }
      }
    }

    it("it does not throw exceptions when there are no dependencies") {
      val injector = Guice.createInjector(new AbstractModule {
        override def configure(): Unit = {
          install(mockModule)
          install(new HealthModule)
        }
      })
      val dynamicHealthDependency = new TypeLiteral[java.util.Map[String, HealthDependency]] {}
      val provider = injector.getInstance(Key.get(dynamicHealthDependency))
      provider mustBe empty
    }
  }
}

class Dependency1 extends HealthDependency {
  override def getName: String = "alpha"

  override def checkAvailability: HealthStatus = ???
}

class DuplicateDependency1 extends HealthDependency {
  override def getName: String = "alpha"

  override def checkAvailability: HealthStatus = ???
}

class Dependency2 extends HealthDependency {
  override def getName: String = "beta"

  override def checkAvailability: HealthStatus = ???
}
