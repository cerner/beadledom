package com.cerner.beadledom.health.dto

import java.time.Instant

import org.scalatest.{FunSpec, MustMatchers}

class ServerDtoSpec extends FunSpec with MustMatchers with JacksonSerializationBehaviors {

  val dto = ServerDto.builder()
    .setHostName("host")
    .setStartupDateTime(Instant.parse("2001-02-03T04:05:06Z")).build()

  it must behave like aJacksonSerializableObject(dto)
}
