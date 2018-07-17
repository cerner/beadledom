package com.cerner.beadledom.health.dto

import org.scalatest.{FunSpec, MustMatchers}

class HttpServiceDtoSpec extends FunSpec with MustMatchers with JacksonSerializationBehaviors {

  val dto = HttpServiceDto.builder()
            .setStatus(200)
            .setUrl("http://www.google.com").build()

  it must behave like aJacksonSerializableObject(dto)
}
