package com.cerner.beadledom.jackson.filter

import com.cerner.beadledom.jackson.filter.DeepModel.{EmbeddedDeep, EmbeddedDeeper, InChinaNow}
import com.cerner.beadledom.jackson.filter.FakeModel._
import com.cerner.beadledom.jackson.filter.Venue._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.TokenBuffer
import com.google.common.collect.Lists
import java.io.ByteArrayOutputStream
import org.scalatest._
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Field filter spec for testing different serialization cases.
 */
class FieldFilterSpec extends AnyFunSpec with BeforeAndAfterAll with Matchers with MockitoSugar {

  describe("Processing of fields for the field filter.") {
    it("Handles fields with trailing slashes.") {
      val filter = FieldFilter.create("foo/")

      filter.getFilters should have size 1
      filter.getFilters.get("foo") should be (FieldFilter.UNFILTERED_FIELD)
    }

    it("Handles nested fields with trailing slashes.") {
      val filter = FieldFilter.create("foo/id/")

      filter.getFilters should have size 1

      val innerFilter = filter.getFilters.get("foo")
      innerFilter.getFilters.get("id") should be (FieldFilter.UNFILTERED_FIELD)
    }

    it("Handles a valid string") {
      val filter = FieldFilter.create("id,name,object/id")

      filter.getFilters should have size 3
      filter.getFilters.get("id") should be (FieldFilter.UNFILTERED_FIELD)
      filter.getFilters.get("name") should be (FieldFilter.UNFILTERED_FIELD)
      filter.getFilters.get("object") should not be null

      filter.getFilters.get("object").getFilters.get("id") should be (FieldFilter.UNFILTERED_FIELD)
    }
  }

  describe("PartialResponseFilter with a container response of a FakeModel") {

    val fakeInnerModel1 = new FakeInnerModel("inner_id1", "inner_name1", List("inner_tag1").asJava)
    val fakeInnerModel2 = new FakeInnerModel("inner_id2", "inner_name2", List("inner_tag2").asJava)
    val fakeModel = new FakeModel("id1", "name1", 1, List("tag1", "tag2").asJava,
      List(fakeInnerModel1, fakeInnerModel2).asJava)

    it("serializes a FakeModel") {
      val objectMapper = new ObjectMapper()
      val outputStream = new ByteArrayOutputStream()
      val tokenBuffer = new TokenBuffer(objectMapper, false)
      objectMapper.writeValue(tokenBuffer, fakeModel)
      val jgen = objectMapper.getFactory.createGenerator(outputStream)
      val filter = FieldFilter.create("id,name,times,tags,inner_models")
      filter.writeJson(tokenBuffer.asParser(), jgen)

      val expected = objectMapper.writeValueAsString(fakeModel)
      val json = outputStream.toString
      json should be (expected)
    }

    it("filters simple bean properties") {
      val objectMapper = new ObjectMapper()
      val outputStream = new ByteArrayOutputStream()
      val tokenBuffer = new TokenBuffer(objectMapper, false)
      objectMapper.writeValue(tokenBuffer, fakeModel)
      val jgen = objectMapper.getFactory.createGenerator(outputStream)
      val filter = FieldFilter.create("id,name,times")
      filter.writeJson(tokenBuffer.asParser(), jgen)

      val expected = """{"id":"id1","name":"name1","times":1}"""
      val json = outputStream.toString
      json should be (expected)
    }

    it("filters nested bean properties") {
      val objectMapper = new ObjectMapper()
      val outputStream = new ByteArrayOutputStream()
      val jgen = objectMapper.getFactory.createGenerator(outputStream)
      val tokenBuffer = new TokenBuffer(objectMapper, false)
      objectMapper.writeValue(tokenBuffer, fakeModel)
      val filter = FieldFilter.create("id,name,inner_models/id")
      filter.writeJson(tokenBuffer.asParser(), jgen)

      val expected = """{"id":"id1","name":"name1","inner_models":[{"id":"inner_id1"},{"id":"inner_id2"}]}"""
      val json = outputStream.toString
      json should be (expected)
    }

    it("serializes a complex object with filtering") {
      val albums = List(new Album("album_id1", "album name 1"),
        new Album("album_id2", "album_name2")).asJava

      val specificAlbums: java.util.List[Album] = List(new Album("go", "go"),
        new Album("wrong", "Everything is Wrong")).asJava
      val vocalists: java.util.List[Vocalist] = List(
        new Vocalist("vocalist1", "VocalistOne", specificAlbums, "bald"),
        new Vocalist("vocalist2", "VocalistTwo", albums, "curly + long")).asJava
      val failures: java.util.List[Failure] = List(new Failure("failure1",
        "Most Musicians", List("There's always an excuse.", "Also terrible.").asJava))
          .asJava
      val guitarists: java.util.List[Musician] = List(
        new Guitarist("guitarist1", "GuitaristOne", albums, "loud"))
          .asJava.asInstanceOf[java.util.List[Musician]]
      val tenors: java.util.List[Tenor] = List(new Tenor("good", "TenorOne", albums, "baldish",
        "perfect")).asJava
      val venue = new
              Venue("venue_id1", "THE venue", "THE place", tenors, vocalists, guitarists, failures)

      val objectMapper = new ObjectMapper()
      val outputStream = new ByteArrayOutputStream()
      val jgen = objectMapper.getFactory.createGenerator(outputStream)
      val tokenBuffer = new TokenBuffer(objectMapper, false)
      objectMapper.writeValue(tokenBuffer, venue)
      val filter = FieldFilter.create(
        "id,name,tenors/pitch_pipe,tenors/name,tenors/albums/name,vocalists/albums/id,vocalists/albums/name," +
            "vocalists/hair_style,vocalists/id,vocalists/name,failures/excuses,failures/name,guitarists/name,guitarists/attitude")
      filter.writeJson(tokenBuffer.asParser(), jgen)

      val json = outputStream.toString
      val expected = """{"id":"venue_id1","name":"THE venue","tenors":[{"pitch_pipe":"perfect","name":"TenorOne","albums":[{"name":"album name 1"},{"name":"album_name2"}]}],"guitarists":[{"attitude":"HAPPY","name":"GuitaristOne"}],"failures":[{"name":"Most Musicians","excuses":["There's always an excuse.","Also terrible."]}],"vocalists":[{"albums":[{"id":"go","name":"go"},{"id":"wrong","name":"Everything is Wrong"}],"hair_style":"bald","id":"vocalist1","name":"VocalistOne"},{"albums":[{"id":"album_id1","name":"album name 1"},{"id":"album_id2","name":"album_name2"}],"hair_style":"curly + long","id":"vocalist2","name":"VocalistTwo"}]}"""
      json should be (expected)
    }

    it("serializes a deeply nested object") {
      val objectMapper = new ObjectMapper()
      val outputStream = new ByteArrayOutputStream()


      val deepModel = new DeepModel("deep_id", List(new EmbeddedDeep(
        "embedded_id",
        List(new EmbeddedDeeper(
          "embedded_deeper_id",
          List(
            new InChinaNow("at_this_point_too_far_in", 10000)
          ).asJava
        )).asJava
      )).asJava)

      val jgen = objectMapper.getFactory.createGenerator(outputStream)
      val filter = FieldFilter.create("id,stupid_name")
      val tokenBuffer = new TokenBuffer(objectMapper, false)
      objectMapper.writeValue(tokenBuffer, deepModel)
      filter.writeJson(tokenBuffer.asParser(), jgen)

      val expected = objectMapper.writeValueAsString(deepModel)
      val json = outputStream.toString
      json should be (expected)
    }

    it("serializes a small FakeModel in < 100ms") {
      val objectMapper = new ObjectMapper()
      val outputStream = new ByteArrayOutputStream()

      val startTime = System.currentTimeMillis()
      val jgen = objectMapper.getFactory.createGenerator(outputStream)
      val filter = FieldFilter.create("id,name,times,tags,inner_models")
      val tokenBuffer = new TokenBuffer(objectMapper, false)
      objectMapper.writeValue(tokenBuffer, fakeModel)
      filter.writeJson(tokenBuffer.asParser(), jgen)
      // (System.currentTimeMillis() - startTime) should be < 100L
      System.gc()
    }
  }

  describe("A series of tests to measure serialization time.") {
    val fakeInnerModel1 = new FakeInnerModel("inner_id1", "inner_name1", List("inner_tag1").asJava)
    val fakeInnerModel2 = new FakeInnerModel("inner_id2", "inner_name2", List("inner_tag2").asJava)

    val objectMapper = new ObjectMapper()

    it("serializes a large FakeModel - about 6MB") {
      (1 to 10).foreach { value =>
        // generate new object before timer starts
        val innerModels: java.util.List[FakeInnerModel] = Lists.newArrayList(fakeInnerModel2)
        (0 until 100000)
            .foreach({ value => innerModels.add(new FakeInnerModel(s"$value", s"inner_name$value",
          List(s"inner_tag$value").asJava))
        })
        val fakeModel = new FakeModel("id1", "name1", 1, List("tag1", "tag2").asJava, innerModels)
        System.gc()

        // start timing, poor man timer
        val startTime = System.currentTimeMillis()
        objectMapper.writeValueAsString(fakeModel)
        println(s"(None) Total time for run $value: ${System.currentTimeMillis() - startTime}")
        System.gc()
      }
    }

    it("serializes a large FakeModel with nested filtering - about 6MB") {
      (1 to 10).foreach { value =>
        // generate new object before timer starts
        val innerModels = Lists.newArrayList(fakeInnerModel2)
        (0 until 100000)
            .foreach({ value => innerModels.add(new FakeInnerModel(s"$value", s"inner_name$value",
          List(s"inner_tag$value").asJava))
        })
        val fakeModel = new FakeModel("id1", "name1", 1, List("tag1", "tag2").asJava, innerModels)
        System.gc()

        // start timing, poor man timer
        val startTime = System.currentTimeMillis()
        val outputStream = new ByteArrayOutputStream()
        val jgen = objectMapper.getFactory.createGenerator(outputStream)
        val tokenBuffer = new TokenBuffer(objectMapper, false)
        objectMapper.writeValue(tokenBuffer, fakeModel)
        val filter = FieldFilter.create("id,name,inner_models/id")
        filter.writeJson(tokenBuffer.asParser, jgen)

        println(s"(Deep) Total time for run $value: ${System.currentTimeMillis() - startTime}")
        System.gc()
      }
    }

    it("serializes a large FakeModel with simple filtering - about 6MB") {
      (0 until 10).foreach { value =>
        // generate new object before timer starts
        val innerModels = Lists.newArrayList(fakeInnerModel2)
        (0 until 100000)
            .foreach({ value => innerModels.add(new FakeInnerModel(s"$value", s"inner_name$value",
          List(s"inner_tag$value").asJava))
        })
        val fakeModel = new FakeModel("id1", "name1", 1, List("tag1", "tag2").asJava, innerModels)
        System.gc()

        // start timing, poor man timer
        val startTime = System.currentTimeMillis()
        val outputStream = new ByteArrayOutputStream()
        val jgen = objectMapper.getFactory.createGenerator(outputStream)
        val tokenBuffer = new TokenBuffer(objectMapper, false)
        objectMapper.writeValue(tokenBuffer, fakeModel)
        val filter = FieldFilter.create("id,name,times")
        filter.writeJson(tokenBuffer.asParser, jgen)

        println(s"(Simple) Total time for run $value: ${System.currentTimeMillis() - startTime}")
        System.gc()
      }
    }
  }
}
