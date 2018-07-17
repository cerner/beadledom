package com.cerner.beadledom.health.dto

import org.scalatest.{FunSpec, MustMatchers}

class BuildDtoSpec extends FunSpec with MustMatchers with JacksonSerializationBehaviors {

  val buildDto = BuildDto.builder()
                 .setArtifactName("some name")
                 .setBuildDateTime("some time")
                 .setVersion("2").build()

  it must behave like aJacksonSerializableObject(buildDto)
}
