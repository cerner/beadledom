package com.cerner.beadledom.health.dto

import com.cerner.beadledom.metadata.{BuildInfo, ServiceMetadata}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}
import java.time.Instant
import scala.collection.JavaConverters._
import java.util.{Optional, List => JList}

class HealthDtoSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("HealthDto") {
    describe(".builder(ServiceMetadata)") {
      it("initializes fields based on the metadata") {
        val buildInfo = mock[BuildInfo]
        val dateTime : Optional[String] = Optional.of("2016-02-03T04:05:06Z")
        when(buildInfo.getBuildDateTime).thenReturn(dateTime)
        when(buildInfo.getVersion).thenReturn("13.0")
        val metadata = ServiceMetadata.builder()
            .setBuildInfo(buildInfo)
            .setHostName("flake002")
            .setStartupTime(Instant.parse("2001-02-03T04:05:06Z"))
            .build()
        val dto = HealthDto.builder(metadata).setStatus(200).build()
        dto.getServer.get().getHostName.get() must be ("flake002")
        dto.getBuild.get().getVersion.get() must be ("13.0")
        dto.getServer.get().getStartupDateTime.get() must be (Instant.parse("2001-02-03T04:05:06Z"))
      }
    }
  }

  describe("HealthDto.Builder") {
    val links = LinksDto.builder().setSelf("localhost").build()
    val dependency = List(HealthDependencyDto.builder()
        .setName("crashmaster")
        .setHealthy(false)
        .setId("flake")
        .setMessage("It's going to take you people years to fix this.")
        .setLinks(links)
        .build()).asJava
    val serverDto = ServerDto
        .builder()
        .setHostName("Node007")
        .setStartupDateTime(Instant.parse("2001-02-03T04:05:06Z"))
        .build()
    val buildDto = BuildDto
        .builder()
        .setArtifactName("YELP")
        .setVersion("13.0")
        .setBuildDateTime("2001-02-03T04:05:06Z")
        .build()

    it("builds an object correctly") {
      val dto = HealthDto.builder()
              .setDependencies(dependency)
              .setbuild(buildDto)
              .setServer(serverDto)
              .setStatus(776)
              .build()

      dto.getDependencies.get() must be (dependency)
      dto.getDependencies.get().get(0).isHealthy mustBe false
      dto.getDependencies.get().get(0).getMessage.get() must be ("It's going to take you people " +
          "years to fix this.")
      dto.getServer.get().getHostName.get() must be ("Node007")
      dto.getServer.get().getStartupDateTime.get() must be (Instant.parse("2001-02-03T04:05:06Z"))
      dto.getStatus must be(776)
      dto.getBuild.get.getVersion.get() must be ("13.0")
    }

    it("allows Optional#empty for non-required fields") {
      val none: Optional[String] = Optional.empty()
      val noneList: Optional[JList[HealthDependencyDto]] = Optional.empty()
      val dto = HealthDto.builder()
          .setStatus(500)
          .setDependencies(noneList)
          .build()
      dto.getDependencies must be (noneList)
    }
  }
}
