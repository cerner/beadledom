package com.cerner.beadledom.jaxrs

import com.cerner.beadledom.jaxrs.provider.{FakeModel, FakeResource}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

import org.jboss.resteasy.core.Dispatcher
import org.jboss.resteasy.mock.{MockDispatcherFactory, MockHttpRequest, MockHttpResponse}
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory
import org.scalatest._

import java.io._

import scala.collection.JavaConverters._
import javax.ws.rs.core.MediaType
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
  * Spec tests for {@link PATCH}.
  *
  * @author Eric Christensen
  */
class PATCHSpec extends AnyFunSpec with BeforeAndAfterAll with Matchers with MockitoSugar {

    val dispatcher: Dispatcher = MockDispatcherFactory.createDispatcher()
    val noDefaults: POJOResourceFactory = new POJOResourceFactory(classOf[FakeResource])
    dispatcher.getRegistry.addResourceFactory(noDefaults)
    dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])

    describe("PATCH") {
      it("the resource is patched correctly") {

        val model = new FakeModel()
            .setId("id")
            .setName("name")
            .setTimes(10)
            .setTags(List.empty[String].asJava)
            .setInnerModels(List.empty[FakeModel.FakeInnerModel].asJava)

        val mapper = new ObjectMapper
        val request: MockHttpRequest = MockHttpRequest.create("PATCH", "/fakeResource/Patch")
        request.contentType(MediaType.APPLICATION_JSON)
        request.content(new ByteArrayInputStream(mapper.writeValueAsBytes(model)))

        val response: MockHttpResponse = new MockHttpResponse()
        dispatcher.invoke(request, response)

        response.getStatus shouldBe 200

        val newModel: FakeModel = mapper.readValue(response.getContentAsString, classOf[FakeModel])
        newModel.name shouldBe "newName"
        newModel.id shouldBe "newId"
        newModel.times shouldBe 10
      }
    }
}
