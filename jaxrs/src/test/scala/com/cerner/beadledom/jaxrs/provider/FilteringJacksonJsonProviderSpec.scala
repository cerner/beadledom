package com.cerner.beadledom.jaxrs.provider

import com.cerner.beadledom.jaxrs.provider.FakeModel.FakeInnerModel
import com.cerner.beadledom.jaxrs.provider.Venue._
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Lists
import java.io.{ByteArrayOutputStream, OutputStream}
import java.nio.charset.Charset
import javax.ws.rs.core._
import org.jboss.resteasy.specimpl.MultivaluedMapImpl
import org.mockito
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatest._
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Spec tests for {@link FilteringJacksonJsonProvider}.
 */
class FilteringJacksonJsonProviderSpec
    extends AnyFunSpec with BeforeAndAfterAll with Matchers with MockitoSugar {

  val objectMapper = new ObjectMapper()

  describe("PartialResponseFilter with a container response of a FakeModel") {
    val fakeInnerModel1 = new FakeInnerModel("inner_id1", "inner_name1", List("inner_tag1").asJava)
    val fakeInnerModel2 = new FakeInnerModel("inner_id2", "inner_name2", List("inner_tag2").asJava)
    val fakeModel = new FakeModel("id1", "name1", 1, List("tag1", "tag2").asJava,
      List(fakeInnerModel1, fakeInnerModel2).asJava)

    it("serializes a FakeModel") {
      val uriInfo = Mockito.mock(classOf[UriInfo])
      Mockito.when(uriInfo.getQueryParameters).thenReturn(new MultivaluedMapImpl[String, String])
      val output = Mockito.mock(classOf[OutputStream])

      val filter = new FilteringJacksonJsonProvider(objectMapper)
      filter.uriInfo = uriInfo

      filter.writeTo(fakeModel,
        fakeModel.getClass,
        null,
        Array(),
        MediaType.APPLICATION_JSON_TYPE,
        null,
        output)

      val json = objectMapper.writeValueAsString(fakeModel)
      val captor = ArgumentCaptor.forClass(classOf[Array[Byte]])
      Mockito.verify(output)
          .write(captor.capture(), mockito.ArgumentMatchers.anyInt(),
            mockito.ArgumentMatchers.anyInt())
      json should be(objectMapper.readTree(captor.getValue).toString)
    }

    it("filters simple bean properties") {
      val uriInfo = Mockito.mock(classOf[UriInfo])
      val queryParams = new MultivaluedMapImpl[String, String]
      queryParams.add("fields", "id,name,times")
      Mockito.when(uriInfo.getQueryParameters).thenReturn(queryParams)
      val output = Mockito.mock(classOf[OutputStream])

      val filter = new FilteringJacksonJsonProvider(objectMapper)
      filter.uriInfo = uriInfo

      filter.writeTo(fakeModel,
        fakeModel.getClass,
        null,
        Array(),
        MediaType.APPLICATION_JSON_TYPE,
        null,
        output)

      val json = """{"id":"id1","name":"name1","times":1}"""
      val captor = ArgumentCaptor.forClass(classOf[Array[Byte]])
      Mockito.verify(output)
          .write(captor.capture(), mockito.ArgumentMatchers.anyInt(),
            mockito.ArgumentMatchers.anyInt())
      json should be(objectMapper.readTree(captor.getValue).toString)
    }

    it("filters nested bean properties") {
      val uriInfo = Mockito.mock(classOf[UriInfo])
      val queryParams = new MultivaluedMapImpl[String, String]
      queryParams.add("fields", "id,name,inner_models/id")
      Mockito.when(uriInfo.getQueryParameters).thenReturn(queryParams)
      val output = new ByteArrayOutputStream()

      val filter = new FilteringJacksonJsonProvider(objectMapper)
      filter.uriInfo = uriInfo

      filter.writeTo(fakeModel,
        fakeModel.getClass,
        null,
        Array(),
        MediaType.APPLICATION_JSON_TYPE,
        null,
        output)

      val json = """{"id":"id1","name":"name1","inner_models":[{"id":"inner_id1"},{"id":"inner_id2"}]}"""
      json should be(output.toString(Charset.defaultCharset().name()))
    }

    it("serializes a complex object with filtering") {
      val albums = List(new Album("album_id1", "album name 1"),
        new Album("album_id2", "album_name2")).asJava

      val mobyAlbums: java.util.List[Album] = List(new Album("go", "go"),
        new Album("wrong", "Everything is Wrong")).asJava
      val vocalists: java.util.List[Vocalist] = List(
        new Vocalist("vocalist1", "VocalistOne", mobyAlbums, "bald"),
        new Vocalist("vocalist2", "VocalistTwo", albums, "curly + long")).asJava
      val failures: java.util.List[Failure] = List(new Failure("failure1",
        "Most Musicians", List("There's always an excuse.", "Also terrible.").asJava))
          .asJava
      val guitarists: java.util.List[Musician] = List(
        new Guitarist("guitarist1", "GuitaristOne", albums, "crazy"))
          .asJava.asInstanceOf[java.util.List[Musician]]
      val tenors: java.util.List[Tenor] = List(new Tenor("tenor-1", "TenorOne", albums, "baldish",
        "perfect")).asJava
      val venue = new
              Venue("venue_id1", "THE venue", "THE place", tenors, vocalists, guitarists, failures)

      val uriInfo = Mockito.mock(classOf[UriInfo])
      val queryParams = new MultivaluedMapImpl[String, String]
      queryParams.add("fields",
        "id,name,tenors/pitch_pipe,tenors/name,tenors/albums/name,vocalists/albums/id,vocalists/albums/name," +
            "vocalists/hair_style,vocalists/id,vocalists/name,failures/excuses,failures/name,guitarists/name,guitarists/attitude")
      Mockito.when(uriInfo.getQueryParameters).thenReturn(queryParams)
      val output = new ByteArrayOutputStream()

      val filter = new FilteringJacksonJsonProvider(objectMapper)
      filter.uriInfo = uriInfo

      filter.writeTo(venue,
        venue.getClass,
        null,
        Array(),
        MediaType.APPLICATION_JSON_TYPE,
        null,
        output)

      val json = """{"id":"venue_id1","name":"THE venue","tenors":[{"pitch_pipe":"perfect","name":"TenorOne","albums":[{"name":"album name 1"},{"name":"album_name2"}]}],"guitarists":[{"attitude":"HAPPY","name":"GuitaristOne"}],"failures":[{"name":"Most Musicians","excuses":["There's always an excuse.","Also terrible."]}],"vocalists":[{"albums":[{"id":"go","name":"go"},{"id":"wrong","name":"Everything is Wrong"}],"hair_style":"bald","id":"vocalist1","name":"VocalistOne"},{"albums":[{"id":"album_id1","name":"album name 1"},{"id":"album_id2","name":"album_name2"}],"hair_style":"curly + long","id":"vocalist2","name":"VocalistTwo"}]}"""
      json should be(output.toString(Charset.defaultCharset().name()))
    }
  }

  describe("A series of tests to measure serialization time.") {
    val fakeInnerModel1 = new FakeInnerModel("inner_id1", "inner_name1", List("inner_tag1").asJava)
    val fakeInnerModel2 = new FakeInnerModel("inner_id2", "inner_name2", List("inner_tag2").asJava)
    val fakeModel = new FakeModel("id1", "name1", 1, List("tag1", "tag2").asJava,
      Lists.newArrayList(fakeInnerModel1, fakeInnerModel2))

    it("serializes a small FakeModel") {
      val uriInfo = Mockito.mock(classOf[UriInfo])
      Mockito.when(uriInfo.getQueryParameters).thenReturn(new MultivaluedMapImpl[String, String])
      val output = Mockito.mock(classOf[OutputStream])

      val filter = new FilteringJacksonJsonProvider(objectMapper)
      filter.uriInfo = uriInfo

      val startTime = System.currentTimeMillis()
      filter.writeTo(fakeModel,
        fakeModel.getClass,
        null,
        Array(),
        MediaType.APPLICATION_JSON_TYPE,
        null,
        output)
    }

    it("serializes a large FakeModel - about 6MB") {
      (0 until 100000).foreach({ value => fakeModel.innerModels.add(fakeInnerModel1) })
      val uriInfo = Mockito.mock(classOf[UriInfo])
      Mockito.when(uriInfo.getQueryParameters).thenReturn(new MultivaluedMapImpl[String, String])

      val filter = new FilteringJacksonJsonProvider(objectMapper)
      filter.uriInfo = uriInfo

      (0 until 10).foreach { value =>
        System.gc()
        val output = new ByteArrayOutputStream()
        val startTime = System.currentTimeMillis()
        filter.writeTo(fakeModel,
          fakeModel.getClass,
          null,
          Array(),
          MediaType.APPLICATION_JSON_TYPE,
          null,
          output)

        System.gc()
        println(s"Total time for large fake model with no filtering ${
          System.currentTimeMillis() - startTime
        }")
      }
    }

    it("serializes a large FakeModel with filtering - about 6MB") {
      (0 until 100000).foreach({ value => fakeModel.innerModels.add(fakeInnerModel1) })
      val uriInfo = Mockito.mock(classOf[UriInfo])
      val queryParams = new MultivaluedMapImpl[String, String]
      queryParams.add("fields", "id,name,inner_models/id")
      Mockito.when(uriInfo.getQueryParameters).thenReturn(queryParams)

      val filter = new FilteringJacksonJsonProvider(objectMapper)
      filter.uriInfo = uriInfo

      (0 until 10).foreach { value =>
        System.gc()
        val output = new ByteArrayOutputStream()

        val startTime = System.currentTimeMillis()
        filter.writeTo(fakeModel,
          fakeModel.getClass,
          null,
          Array(),
          MediaType.APPLICATION_JSON_TYPE,
          null,
          output)

        System.gc()
        println(s"Total time for large fake model with filtering ${
          System.currentTimeMillis() - startTime
        }")
      }
    }
  }
}
