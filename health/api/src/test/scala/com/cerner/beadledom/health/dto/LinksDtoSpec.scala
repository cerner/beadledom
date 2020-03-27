package com.cerner.beadledom.health.dto

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class LinksDtoSpec extends AnyFunSpec with Matchers with JacksonSerializationBehaviors {

  val dto = LinksDto.builder()
    .setSelf("self").build()

  it must behave like aJacksonSerializableObject(dto)
}
