package com.cerner.beadledom.health.dto

import java.util.Optional

import org.scalatest.{FunSpec, MustMatchers}

class HealthDependencyDtoSpec extends FunSpec with MustMatchers with JacksonSerializationBehaviors {
  describe("HealthDependencyDto.Builder") {
    it("builds an object correctly") {
      val links = LinksDto.builder().setSelf("localhost").build()
      val dto = HealthDependencyDto.builder()
        .setHealthy(true)
        .setId("pikachu")
        .setName("pikachu")
        .setMessage("Go to catch them all!!")
        .setPrimary(true)
        .setLinks(links)
        .build()
      dto.isHealthy mustBe true
      dto.isPrimary mustBe true
      dto.getId must be("pikachu")
      dto.getMessage.get() must be("Go to catch them all!!")
      dto.getName.get() must be("pikachu")
      dto.getLinks.get().getSelf must be("localhost")
    }

    it("allows Optional#empty for non-required fields") {
      val none: Optional[String] = Optional.empty()
      val noneInt: Optional[Integer] = Optional.empty()
      val noneBoolean: Optional[Boolean] = Optional.empty()
      val links = LinksDto.builder().setSelf("localhost").build()
      val dto = HealthDependencyDto.builder()
        .setHealthy(true)
        .setId("pikachu")
        .setName(none)
        .setMessage(none)
        .setLinks(links)
        .build()
      dto.getMessage must be(none)
      dto.getName must be(none)
    }

    it("sets the default values for non-required fields") {
      val none = Optional.empty()
      val dto = HealthDependencyDto
        .builder()
        .setId("Optional")
        .build()
      dto.isPrimary must be(false)
      dto.isHealthy must be(false)
      dto.getLinks must be(none)
      dto.getType must be(none)
      dto.getMessage must be(none)
      dto.getName must be(none)
    }
  }

  val healthDependencyDto = HealthDependencyDto.builder()
    .setHealthy(true)
    .setId("1")
    .setName("name")
    .setMessage("message")
    .setPrimary(true)
    .setLinks(LinksDto.builder().setSelf("link").build())
    .build()

  it must behave like aJacksonSerializableObject(healthDependencyDto)
}
