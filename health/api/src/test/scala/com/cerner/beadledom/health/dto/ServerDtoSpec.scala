package com.cerner.beadledom.health.dto

import java.time.Instant

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class ServerDtoSpec extends AnyFunSpec with Matchers with JacksonSerializationBehaviors {

  val dto = ServerDto.builder()
    .setHostName("host")
    .setStartupDateTime(Instant.parse("2001-02-03T04:05:06Z")).build()

  it must behave like aJacksonSerializableObject(dto)
}
