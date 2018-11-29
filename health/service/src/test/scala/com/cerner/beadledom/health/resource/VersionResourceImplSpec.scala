package com.cerner.beadledom.health.resource

import java.time.Instant
import java.util.Properties

import com.cerner.beadledom.metadata.{BuildInfo, ServiceMetadata}
import com.github.mustachejava.DefaultMustacheFactory
import org.scalatest._

/**
  * Spec tests for VersionResourceImpl.
  *
  * @author Nimesh Subramanian
  */
class VersionResourceImplSpec extends FunSpec with MustMatchers {
  describe("VersionResourceImpl") {
    describe("#getVersionInfo") {
      it("returns a dto with status 200") {
        val serviceMetadata = ServiceMetadata.builder()
          .setBuildInfo(BuildInfo.builder()
            .setArtifactId("artifactId")
            .setBuildDateTime("Charmander")
            .setScmRevision("slowpoke")
            .setGroupId("groupId")
            .setRawProperties(new Properties)
            .setVersion("version")
            .build())
          .setStartupTime(Instant.now())
          .build()

        val mustacheFactory = new DefaultMustacheFactory("com/cerner/beadledom/health")

        val resource = new VersionResourceImpl(serviceMetadata, mustacheFactory)

        val buildDto = resource.getVersionInfo

        buildDto.getVersion.get() must be("version")
        buildDto.getArtifactName.get() must be("artifactId")
        buildDto.getBuildDateTime.get() must be("Charmander")
      }
    }
  }
}
