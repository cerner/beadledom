package com.cerner.beadledom.health.dto

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class HttpServiceDtoSpec extends AnyFunSpec with Matchers with JacksonSerializationBehaviors {

  val dto = HttpServiceDto.builder()
            .setStatus(200)
            .setUrl("http://www.google.com").build()

  it must behave like aJacksonSerializableObject(dto)
}
