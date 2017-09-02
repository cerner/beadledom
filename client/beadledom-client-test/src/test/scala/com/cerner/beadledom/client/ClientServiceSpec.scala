package com.cerner.beadledom.client

import com.cerner.beadledom.client.example.client._
import com.cerner.beadledom.client.example.model.{JsonOne, JsonTwo}
import com.cerner.beadledom.client.example.{ResourceOne, ResourceTwo}
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.google.inject._
import org.scalatest.{BeforeAndAfter, DoNotDiscover, FunSpec, MustMatchers}

/**
 * Specs to test the Clients of a service.
 */
@DoNotDiscover
class ClientServiceSpec(contextRoot: String, servicePort: Int)
    extends FunSpec with MustMatchers with BeforeAndAfter {
  val baseUri = s"http://localhost:$servicePort$contextRoot"

  def getInjector(modules: List[Module]): Injector = {
    val module = new AbstractModule() {
      override def configure(): Unit = {
        modules.foreach(m => install(m))

        bind(classOf[ExampleClientConfig]).toInstance(new ExampleClientConfig(baseUri))
      }
    }
    Guice.createInjector(module)
  }

  describe("Proxied Clients") {
    describe("support two clients at once") {
      it("retrieves the resources from different clients") {
        val injector = getInjector(List(new ResourceOneModule, new ResourceTwoModule))

        val resourceOne = injector.getInstance(classOf[ResourceOne])
        val resourceTwo = injector.getInstance(classOf[ResourceTwo])

        val jsonNewOne = JsonOne.create("New Json", "Hola1")
        val jsonOne = JsonOne.create("LocalOne", "Hi")
        resourceOne.echo(jsonOne) mustBe jsonOne
        resourceOne.patch(jsonOne) mustBe jsonNewOne

        val jsonNewTwo = JsonTwo.create("New Json", "Hola2")
        val jsonTwo = JsonTwo.create("LocalTwo", "Howdy")
        resourceTwo.echo(jsonTwo) mustBe jsonTwo
        resourceTwo.patch(jsonTwo) mustBe jsonNewTwo
      }

      it("each client gets its own unique object mapper") {
        val injector = getInjector(List(new ResourceOneModule, new ResourceTwoModule))

        val mapperOne = injector
            .getInstance(Key.get(classOf[ObjectMapper], classOf[ResourceOneFeature]))
        val mapperTwo = injector
            .getInstance(Key.get(classOf[ObjectMapper], classOf[ResourceTwoFeature]))

        mapperOne.isEnabled(SerializationFeature.INDENT_OUTPUT) must be(false)
        mapperTwo.isEnabled(SerializationFeature.INDENT_OUTPUT) must be(true)
      }

      it("provides default object mapper") {
        val injector = getInjector(List(new ResourceOneModule, new ResourceTwoModule))

        val mapper = injector.getInstance(Key.get(classOf[ObjectMapper]))

        mapper.isEnabled(SerializationFeature.INDENT_OUTPUT) must be(false)
      }
    }

    it("Uses the configured client configuration") {
      val injector = getInjector(List(new ResourceOneModule))
      val beadledomClientConfiguration = injector
          .getInstance(Key.get(classOf[BeadledomClientConfiguration], classOf[ResourceOneFeature]))

      beadledomClientConfiguration.connectionPoolSize() must be(60)
      beadledomClientConfiguration.maxPooledPerRouteSize() must be(60)
      beadledomClientConfiguration.socketTimeoutMillis() must be(60)
      beadledomClientConfiguration.connectionTimeoutMillis() must be(60)
      beadledomClientConfiguration.ttlMillis() must be(60)
    }
  }
}
