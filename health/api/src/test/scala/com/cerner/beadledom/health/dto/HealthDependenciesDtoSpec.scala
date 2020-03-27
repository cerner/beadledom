package com.cerner.beadledom.health.dto


import scala.collection.JavaConverters._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

class HealthDependenciesDtoSpec extends AnyFunSpec with Matchers with JacksonSerializationBehaviors {

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
