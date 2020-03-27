package com.cerner.beadledom.avro

import com.cerner.beadledom.metadata.BuildInfo
import com.fasterxml.jackson.databind.{Module, ObjectMapper}
import com.google.inject._
import com.google.inject.multibindings.ProvidesIntoSet
import java.util.Properties
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Spec tests for {@link AvroJacksonGuiceModule}.
 */
class AvroJacksonGuiceModuleSpec extends AnyFunSpec with Matchers with MockitoSugar {
  val injector = Guice.createInjector(new AbstractModule() {
    override def configure(): Unit = {
      val buildInfo = BuildInfo.builder()
          .setVersion("1.0")
          .setArtifactId("artifactId")
          .setGroupId("groupId")
          .setScmRevision("ScmRevision")
          .setRawProperties(new Properties())
          .build()
      bind(classOf[BuildInfo]).toInstance(buildInfo)

      install(new AvroJacksonGuiceModule())
    }

    @Provides
    @Singleton
    def provideObjectMapper(
        jacksonModules: java.util.Set[Module]): ObjectMapper = {
      val objectMapper: ObjectMapper = new ObjectMapper
      objectMapper.registerModules(jacksonModules)
      objectMapper
    }

    @ProvidesIntoSet
    def provideAvroJacksonModule(): Module = {
      return new TestModule
    }
  })

  val mapper = injector.getInstance(classOf[ObjectMapper])

  describe("AvroSerializationModule") {
    describe("ObjectMapper") {
      it("serializes and deserializes avro objects") {
        val model = OuterTestModel.newBuilder
            .setInnerModels(List(
          InnerTestModel.newBuilder
              .setSomeField("what up")
              .build,
          InnerTestModel.newBuilder
              .setNullableWithDefault(null)
              .setSomeField("howdy")
              .build,
          InnerTestModel.newBuilder
              .setNullableWithDefault("hi there")
              .setSomeField("aloha")
              .build).asJava)
            .setLongWithoutDefault(9)
            .setStringWithoutDefault("yo")
            .build
        val str = mapper.writeValueAsString(model)
        mapper.readValue(str, classOf[OuterTestModel]) should be(model)
      }

      it("applies default field values when parsing into avro objects") {
        val input =
          """
            |{
            | "innerModels": [
            |   {
            |     "nullableWithDefault": "hi there",
            |     "someField": "aloha"
            |   },
            |   {
            |     "nullableWithDefault": null,
            |     "someField": "sayonara"
            |   },
            |   {
            |     "someField": "toodles"
            |   }
            | ],
            | "longWithoutDefault": 9,
            | "stringWithoutDefault": "yo"
            |}
          """.stripMargin
        val expected = OuterTestModel.newBuilder
            .setInnerModels(List(
          InnerTestModel.newBuilder
              .setNullableWithDefault("hi there")
              .setSomeField("aloha")
              .build,
          InnerTestModel.newBuilder
              .setNullableWithDefault(null)
              .setSomeField("sayonara")
              .build,
          InnerTestModel.newBuilder
              .setNullableWithDefault(null)
              .setSomeField("toodles")
              .build).asJava)
            .setLongWithoutDefault(9)
            .setLongWithDefault(5)
            .setStringWithoutDefault("yo")
            .build
        mapper.readValue(input, classOf[OuterTestModel]) should be(expected)
      }
    }

    it("creates map of dependencies") {
      val setType = new TypeLiteral[java.util.Set[Module]] {}
      val dependencies = injector.getInstance(Key.get(setType))
      dependencies should have size 2
    }
  }
}
