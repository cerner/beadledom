package com.cerner.beadledom.health.dto

import org.scalatest.{FunSpec, MustMatchers}

class TypeDtoSpec extends FunSpec with MustMatchers with JacksonSerializationBehaviors {

  val httpServiceDto = HttpServiceDto.builder()
    .setStatus(200)
    .setUrl("http://www.google.com").build()

  val dto = TypeDto.builder()
    .setHttpService(httpServiceDto).build()

  it must behave like aJacksonSerializableObject(dto)
}
