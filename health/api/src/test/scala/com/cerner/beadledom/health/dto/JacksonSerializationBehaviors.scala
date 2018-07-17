package com.cerner.beadledom.health.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.scalatest.{FunSpec, MustMatchers}

/**
  * Testing behaviors Jackson JSON serialization
  *
  * @author Nathan Schile
  */
trait JacksonSerializationBehaviors { this: FunSpec with MustMatchers =>

  val mapper = new ObjectMapper()
  mapper.registerModule(new Jdk8Module)
  mapper.registerModule(new JavaTimeModule)

  def aJacksonSerializableObject(dto: => Object) {
    it(s"deserializes JSON to a ${dto.getClass.getSimpleName}") {
      val str = mapper.writeValueAsString(dto)
      mapper.readValue(str, dto.getClass) must be(dto)
    }
  }
}
