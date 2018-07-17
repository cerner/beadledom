package com.cerner.beadledom.health.dto

import org.scalatest.{FunSpec, MustMatchers}

class LinksDtoSpec extends FunSpec with MustMatchers with JacksonSerializationBehaviors {

  val dto = LinksDto.builder()
    .setSelf("self").build()

  it must behave like aJacksonSerializableObject(dto)
}
