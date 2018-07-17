package com.cerner.beadledom.health.dto

import java.time.Instant
import java.util.{Optional, List => JList}

import com.cerner.beadledom.metadata.{BuildInfo, ServiceMetadata}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

import scala.collection.JavaConverters._

class HealthDtoSpec extends FunSpec with MustMatchers with MockitoSugar {
  describe("HealthDto") {
    describe(".builder(ServiceMetadata)") {
      it("initializes fields based on the metadata") {
        val buildInfo = mock[BuildInfo]
        val dateTime: Optional[String] = Optional.of("2016-02-03T04:05:06Z")
        when(buildInfo.getBuildDateTime).thenReturn(dateTime)
        when(buildInfo.getVersion).thenReturn("13.0")
        val metadata = ServiceMetadata.builder()
          .setBuildInfo(buildInfo)
          .setHostName("flake002")
          .setStartupTime(Instant.parse("2001-02-03T04:05:06Z"))
          .build()
        val dto = HealthDto.builder(metadata).setStatus(200).build()
        dto.getServer.get().getHostName.get() must be("flake002")
        dto.getBuild.get().getVersion.get() must be("13.0")
        dto.getServer.get().getStartupDateTime.get() must be(Instant.parse("2001-02-03T04:05:06Z"))
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

    val dto = HealthDto.builder()
      .setDependencies(dependency)
      .setbuild(buildDto)
      .setServer(serverDto)
      .setStatus(776)
      .build()

    it("builds an object correctly") {
      dto.getDependencies.get() must be(dependency)
      dto.getDependencies.get().get(0).isHealthy mustBe false
      dto.getDependencies.get().get(0).getMessage.get() must be("It's going to take you people " +
        "years to fix this.")
      dto.getServer.get().getHostName.get() must be("Node007")
      dto.getServer.get().getStartupDateTime.get() must be(Instant.parse("2001-02-03T04:05:06Z"))
      dto.getStatus must be(776)
      dto.getBuild.get.getVersion.get() must be("13.0")
    }

    // Can't use JacksonSerializationBehaviors because the status field isn't serialized into JSON
    it(s"deserializes JSON to a ${dto.getClass.getSimpleName}") {
      val mapper = new ObjectMapper()
      mapper.registerModule(new Jdk8Module)
      mapper.registerModule(new JavaTimeModule)
      val str = mapper.writeValueAsString(dto)
      val deserializedDto = mapper.readValue(str, dto.getClass)
      deserializedDto.getBuild must be(dto.getBuild)
      deserializedDto.getDependencies must be(dto.getDependencies)
      deserializedDto.getMessage must be(dto.getMessage)
      deserializedDto.getServer must be(dto.getServer)
    }

    it("allows Optional#empty for non-required fields") {
      val none: Optional[String] = Optional.empty()
      val noneList: Optional[JList[HealthDependencyDto]] = Optional.empty()
      val dto = HealthDto.builder()
        .setStatus(500)
        .setDependencies(noneList)
        .build()
      dto.getDependencies must be(noneList)
    }
  }
}
