package com.cerner.beadledom.health.dto

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class TypeDtoSpec extends AnyFunSpec with Matchers with JacksonSerializationBehaviors {

  val httpServiceDto = HttpServiceDto.builder()
    .setStatus(200)
    .setUrl("http://www.google.com").build()

  val dto = TypeDto.builder()
    .setHttpService(httpServiceDto).build()

  it must behave like aJacksonSerializableObject(dto)
}
