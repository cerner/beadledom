package com.cerner.beadledom.health.dto

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class BuildDtoSpec extends AnyFunSpec with Matchers with JacksonSerializationBehaviors {

  val buildDto = BuildDto.builder()
                 .setArtifactName("some name")
                 .setBuildDateTime("some time")
                 .setVersion("2").build()

  it must behave like aJacksonSerializableObject(buildDto)
}
