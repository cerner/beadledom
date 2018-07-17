package com.cerner.beadledom.health.dto

import org.scalatest.{FunSpec, MustMatchers}

import scala.collection.JavaConverters._

class HealthDependenciesDtoSpec extends FunSpec with MustMatchers with JacksonSerializationBehaviors {

  val healthDependency = HealthDependencyDto.builder()
                         .setHealthy(true)
                         .setId("1")
                         .setName("name")
                         .setMessage("message")
                         .setPrimary(true)
                         .setLinks(LinksDto.builder().setSelf("link").build())
                         .build()

  var healthDependenciesDto = HealthDependenciesDto.builder()
                              .setDependencies(List(healthDependency).asJava).build()

  it must behave like aJacksonSerializableObject(healthDependenciesDto)
}
