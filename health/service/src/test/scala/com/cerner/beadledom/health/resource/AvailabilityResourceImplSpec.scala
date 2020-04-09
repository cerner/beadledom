package com.cerner.beadledom.health.resource

import com.cerner.beadledom.metadata.{BuildInfo, ServiceMetadata}
import com.github.mustachejava.DefaultMustacheFactory
import org.scalatest._
import java.time.Instant
import java.util.{Properties, Optional}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec tests for AvailabilityResourceImpl.
 *
 * @author John Leacox
 */
class AvailabilityResourceImplSpec extends AnyFunSpec with Matchers {
  describe("AvailabilityResourceImpl") {
    describe("#getBasicAvailabilityCheck") {
      it("returns a dto with status 200") {
        val serviceMetadata = ServiceMetadata.builder()
        .setBuildInfo(BuildInfo.builder()
            .setArtifactId("artifactId")
            .setGroupId("groupId")
            .setScmRevision("Abomasnow")
            .setBuildDateTime("Alakazam")
            .setRawProperties(new Properties)
            .setVersion("version")
            .build())
          .setStartupTime(Instant.now())
          .build()

        val mustacheFactory = new DefaultMustacheFactory("com/cerner/beadledom/health")

        val resource = new AvailabilityResourceImpl(serviceMetadata, mustacheFactory)

        val healthDto = resource.getBasicAvailabilityCheck

        healthDto.getStatus must be (200)
        healthDto.getMessage must be (Optional.of("artifactId is available"))
      }
    }
  }
}
