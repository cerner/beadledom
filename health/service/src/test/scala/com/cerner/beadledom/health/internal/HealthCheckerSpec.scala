package com.cerner.beadledom.health.internal

import java.lang
import java.net.URI
import java.time.Instant
import java.util.Optional

import com.cerner.beadledom.health.dto.{HealthDependencyDto, HealthDto, LinksDto}
import com.cerner.beadledom.health.{HealthDependency, HealthStatus}
import com.cerner.beadledom.metadata.{BuildInfo, ServiceMetadata}
import javax.ws.rs.WebApplicationException
import org.jboss.resteasy.spi.ResteasyUriInfo
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

import scala.collection.JavaConverters._

class HealthCheckerSpec extends FunSpec with MustMatchers with MockitoSugar {
  val buildInfo = mock[BuildInfo]
  val dateTime: Optional[String] = Optional.of("2016-02-03T04:05:06Z")
  when(buildInfo.getBuildDateTime).thenReturn(dateTime)
  when(buildInfo.getVersion).thenReturn("13.0")
  when(buildInfo.getArtifactId).thenReturn("SampleArtifact")
  when(buildInfo.getVersion).thenReturn("13.0")
  val metadata = ServiceMetadata.builder()
    .setBuildInfo(buildInfo)
    .setHostName("flake002")
    .setStartupTime(Instant.parse("2001-02-03T04:05:06Z"))
    .build()
  val testException = new IllegalArgumentException("it is illegal to argue about HTTP status codes")

  def newChecker(requestUri: String, dependencies: List[HealthDependency]) = new HealthChecker(
    new ResteasyUriInfo(new URI(requestUri)),
    metadata,
    dependencies.groupBy(d => d.getName).mapValues(ds => ds.head).asJava
  )

  def newDependency(name: String, desc: String,
                    status: HealthStatus, primary: lang.Boolean) = new HealthDependency {
    override def getName: String = name

    override def getDescription: Optional[String] = Optional.ofNullable(desc)

    override def checkAvailability(): HealthStatus = status

    override def getPrimary: lang.Boolean = primary
  }

  def faultyDependency = new HealthDependency {
    override def getName: String = "alpha"

    override def getBasicAvailabilityUrl: Optional[String] = Optional
      .ofNullable("faulty_dependency")

    override def checkAvailability(): HealthStatus = throw testException
  }

  describe("HealthChecker") {
    describe("#doPrimaryHealthCheck") {
      it("creates success dto when all primary dependencies are healthy") {
        val checker = newChecker(
          "http://localhost/meta/health",
          List(
            newDependency("alpha", "alpha health check",
              HealthStatus.create(200, "A-OK"), true),
            newDependency("beta", "beta health check",
              HealthStatus.create(299, "sure whatever"), true),
            newDependency("charlie", "charlie health check",
              HealthStatus.create(503, "Don't feel so good"), false)))
        val links1 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/alpha")
          .build()
        val dependency1 = HealthDependencyDto.builder()
          .setName("alpha health check")
          .setHealthy(true)
          .setPrimary(true)
          .setId("alpha")
          .setMessage("A-OK")
          .setLinks(links1)
          .build()
        val links2 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/beta")
          .build()
        val dependency2 = HealthDependencyDto.builder()
          .setName("beta health check")
          .setHealthy(true)
          .setPrimary(true)
          .setId("beta")
          .setMessage("sure whatever")
          .setLinks(links2)
          .build()
        val dto = checker.doPrimaryHealthCheck()
        val expected = HealthDto
          .builder(metadata)
          .setDependencies(List(dependency1, dependency2).asJava)
          .setStatus(200)
          .setMessage("SampleArtifact is available")
          .build()
        dto mustBe expected
      }

      it("creates failure dto when any primary dependencies are unhealthy") {
        val checker = newChecker(
          "http://localhost/meta/health",
          List(
            newDependency("alpha", "alpha health check",
              HealthStatus.create(200, "A-OK"), true),
            newDependency("beta", null,
              HealthStatus.create(500, "Moltres"), true),
            newDependency("charlie", "charlie health check",
              HealthStatus.create(200, "A-OK"), false)))
        val dto = checker.doPrimaryHealthCheck()
        val links1 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/alpha")
          .build()
        val links2 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/beta")
          .build()
        val dependency1 = HealthDependencyDto.builder()
          .setHealthy(true)
          .setPrimary(true)
          .setId("alpha")
          .setName("alpha health check")
          .setMessage("A-OK")
          .setLinks(links1)
          .build()
        val dependency2 = HealthDependencyDto.builder()
          .setHealthy(false)
          .setPrimary(true)
          .setId("beta")
          .setMessage("Moltres")
          .setLinks(links2)
          .build()
        val expected = HealthDto.builder(metadata)
          .setDependencies(List(dependency1, dependency2).asJava)
          .setStatus(503)
          .setMessage("SampleArtifact is unavailable")
          .build()

        dto mustBe expected
      }

      it("includes exception stack trace when illegal argument exception occurs") {
        val checker = newChecker(
          "http://localhost/meta/health",
          List(newDependency("alpha", "alpha",
            HealthStatus.create(300, "not feeling so hot", testException), true))
        )
        val dto = checker.doPrimaryHealthCheck()
        dto.getStatus must be(503)
        dto.getMessage.get() must be("SampleArtifact is unavailable")
        dto.getDependencies.get().get(0).getMessage.get()
          .contains("java.lang.IllegalArgumentException") mustBe true
      }
    }

    describe("#doDiagnosticHealthCheck") {
      it("creates success dto when all dependencies are healthy") {
        val checker = newChecker(
          "http://localhost/meta/health/diagnostic",
          List(
            newDependency("alpha", "alpha health check",
              HealthStatus.create(200, "A-OK"), false),
            newDependency("beta", "beta health check", HealthStatus.create(299, "ratata"), false)))
        val dto = checker.doDiagnosticHealthCheck()
        val links1 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/alpha")
          .build()
        val links2 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/beta")
          .build()
        val dependency1 = HealthDependencyDto.builder()
          .setHealthy(true)
          .setPrimary(false)
          .setId("alpha")
          .setName("alpha health check")
          .setMessage("A-OK")
          .setLinks(links1)
          .build()
        val dependency2 = HealthDependencyDto.builder()
          .setHealthy(true)
          .setPrimary(false)
          .setId("beta")
          .setName("beta health check")
          .setMessage("ratata")
          .setLinks(links2)
          .build()
        val expected = HealthDto.builder(metadata)
          .setDependencies(List(dependency1, dependency2).asJava)
          .setStatus(200)
          .setMessage("SampleArtifact is available")
          .build()
        dto mustBe expected
      }

      it("creates failure dto when any dependencies are unhealthy") {
        val checker = newChecker(
          "http://localhost/meta/health/diagnostic",
          List(
            newDependency("alpha", null,
              HealthStatus.create(200, "A-OK"), false),
            newDependency("beta", null, HealthStatus.create(500, "Moltres"), true)))
        val dto = checker.doDiagnosticHealthCheck()
        val links1 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/alpha")
          .build()
        val links2 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/beta")
          .build()
        val dependency1 = HealthDependencyDto.builder()
          .setHealthy(true)
          .setPrimary(false)
          .setId("alpha")
          .setMessage("A-OK")
          .setLinks(links1)
          .build()
        val dependency2 = HealthDependencyDto.builder()
          .setHealthy(false)
          .setPrimary(true)
          .setId("beta")
          .setMessage("Moltres")
          .setLinks(links2)
          .build()
        val expected = HealthDto.builder(metadata)
          .setDependencies(List(dependency1, dependency2).asJava)
          .setStatus(503)
          .setMessage("SampleArtifact is unavailable")
          .build()
        dto mustBe expected
      }

      it("includes exception stack trace when illegal argument exception occurs") {
        val checker = newChecker(
          "http://localhost/meta/health/diagnostic",
          List(newDependency("alpha", null,
            HealthStatus.create(300, "not feeling so hot", testException), false))
        )
        val dto = checker.doDiagnosticHealthCheck()
        dto.getStatus must be(503)
        dto.getMessage.get() must be("SampleArtifact is unavailable")
        dto.getDependencies.get().get(0).getMessage.get()
          .contains("java.lang.IllegalArgumentException") mustBe true
      }
    }

    describe("#doDependencyListing") {
      it("creates list of all dependencies") {
        var checked: Boolean = false

        def noCheckDependency(name: String, url: String) = new HealthDependency {
          override def getName: String = name

          override def getBasicAvailabilityUrl: Optional[String] = Optional.ofNullable(url)

          override def checkAvailability(): HealthStatus = {
            checked = true
            fail("Dependency was checked")
          }
        }

        val checker = newChecker(
          "http://localhost/meta/health/diagnostic/dependencies",
          List(
            noCheckDependency("alpha", "ext1"),
            noCheckDependency("beta", null)))
        val links1 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/alpha")
          .build()
        val links2 = LinksDto.builder()
          .setSelf("http://localhost/meta/health/diagnostic/dependencies/beta")
          .build()
        val dependency1 = HealthDependencyDto.builder()
          .setId("alpha")
          .setPrimary(true)
          .setLinks(links1)
          .build()
        val dependency2 = HealthDependencyDto.builder()
          .setId("beta")
          .setPrimary(true)
          .setLinks(links2)
          .build()
        val expected = List(dependency1, dependency2)
        checker.doDependencyListing() must contain theSameElementsAs (expected)
      }
    }

    describe("#doDependencyAvailabilityCheck") {
      it("throws 404 exception if name is not recognized") {
        val checker = newChecker(
          "http://localhost/meta/health",
          List(newDependency("beta", null, HealthStatus.create(299, "sure whatever"), false)))
        val expected = the[WebApplicationException] thrownBy
          checker.doDependencyAvailabilityCheck("alpha")
        expected.getResponse.getStatus mustBe 404
      }

      it("builds a correct dto") {
        val checker = newChecker(
          "http://localhost/meta/health/diagnostic/dependency/alpha",
          List(
            newDependency("alpha", null, HealthStatus.create(901, "huh?"), false),
            newDependency("beta", null, HealthStatus.create(299, "sure whatever"), false)))
        val expected = HealthDependencyDto.builder()
          .setMessage("huh?")
          .setId("alpha")
          .setPrimary(false)
          .setHealthy(false)
          .setLinks(LinksDto.builder()
            .setSelf("http://localhost/meta/health/diagnostic/dependencies/alpha").build())
          .build()
        checker.doDependencyAvailabilityCheck("alpha") mustBe expected
      }

      it("includes exception stack trace when illegal argument exception occurs") {
        val checker = newChecker(
          "http://localhost/meta/health",
          List(newDependency("alpha", null,
            HealthStatus.create(300, "not feeling so hot", testException), true))
        )
        val dto = checker.doPrimaryHealthCheck()

        checker.doDependencyAvailabilityCheck("alpha")
          .getMessage.get().contains("java.lang.IllegalArgumentException") mustBe true
      }
    }
  }
}
