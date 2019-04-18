package com.cerner.beadledom.client

import com.cerner.beadledom.client.example.client._
import com.cerner.beadledom.client.example.model.{JsonOne, JsonOneOffsetPaginatedListDto, JsonTwo}
import com.cerner.beadledom.client.example.PaginatedClientResource
import com.cerner.beadledom.jaxrs.GenericResponse
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
        val injector = getInjector(List(new ExampleOneClientModule, new ExampleTwoClientModule))

        val clientOne = injector.getInstance(classOf[ExampleOneClient])
        val clientTwo = injector.getInstance(classOf[ExampleTwoClient])

        val jsonNewOne = JsonOne.create("New Json", "Hola1")
        val jsonOne = JsonOne.create("LocalOne", "Hi")
        clientOne.resourceOne.echo(jsonOne) mustBe jsonOne
        clientOne.resourceOne.patch(jsonOne) mustBe jsonNewOne

        val jsonNewTwo = JsonTwo.create("New Json", "Hola2")
        val jsonTwo = JsonTwo.create("LocalTwo", "Howdy")
        clientTwo.resourceTwo.echo(jsonTwo) mustBe jsonTwo
        clientTwo.resourceTwo.patch(jsonTwo) mustBe jsonNewTwo
      }

      it("each client gets its own unique object mapper") {
        val injector = getInjector(List(new ExampleOneClientModule, new ExampleTwoClientModule))

        val mapperOne = injector
            .getInstance(Key.get(classOf[ObjectMapper], classOf[ResourceOneFeature]))
        val mapperTwo = injector
            .getInstance(Key.get(classOf[ObjectMapper], classOf[ResourceTwoFeature]))

        mapperOne.isEnabled(SerializationFeature.INDENT_OUTPUT) must be(false)
        mapperTwo.isEnabled(SerializationFeature.INDENT_OUTPUT) must be(true)
      }

      it("provides default object mapper") {
        val injector = getInjector(List(new ExampleOneClientModule, new ExampleTwoClientModule))

        val mapper = injector.getInstance(Key.get(classOf[ObjectMapper]))

        mapper.isEnabled(SerializationFeature.INDENT_OUTPUT) must be(false)
      }
    }

    describe("Pagination") {
      it("injects pagination links to the response") {
        val injector = getInjector(List(new ExampleOneClientModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results : JsonOneOffsetPaginatedListDto = paginatedResource.index(0L, 10).body()

        results mustNot be(null)
        results.totalResults() mustBe 1000
        results.items().get(0).getHello mustBe "Hello World"
        results.firstLink() mustBe s"$baseUri/paginated?offset=0&limit=10"
        results.lastLink() mustBe s"$baseUri/paginated?offset=990&limit=10"
        results.prevLink() mustBe null
        results.nextLink() mustBe s"$baseUri/paginated?offset=10&limit=10"
      }

      it("injects previous link when beyond the first page") {
        val injector = getInjector(List(new ExampleOneClientModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results : JsonOneOffsetPaginatedListDto = paginatedResource.index(1L, 10).body()

        results mustNot be(null)
        results.totalResults() mustBe 1000
        results.items().get(0).getHello mustBe "Hello World"
        results.firstLink() mustBe s"$baseUri/paginated?offset=0&limit=10"
        results.lastLink() mustBe s"$baseUri/paginated?offset=990&limit=10"
        results.prevLink() mustBe s"$baseUri/paginated?offset=0&limit=10"
        results.nextLink() mustBe s"$baseUri/paginated?offset=11&limit=10"
      }

      it("rejects 0 limit by default") {
        val injector = getInjector(List(new ExampleOneClientModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results = paginatedResource.index(1L, 0)

        results mustNot be(null)
        results.getStatus mustBe 400
      }

      it("rejects negative offset") {
        val injector = getInjector(List(new ExampleOneClientModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results : GenericResponse[JsonOneOffsetPaginatedListDto] = paginatedResource.index(-1L, 1)

        results mustNot be(null)
        results.getStatus mustBe 400
      }

      it("rejects negative limits") {
        val injector = getInjector(List(new ExampleOneClientModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results : GenericResponse[JsonOneOffsetPaginatedListDto] = paginatedResource.index(1L, -10)

        results mustNot be(null)
        results.getStatus mustBe 400
      }

      it("rejects limits over the max") {
        val injector = getInjector(List(new ExampleOneClientModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results : GenericResponse[JsonOneOffsetPaginatedListDto] = paginatedResource.index(1L, 20)

        results mustNot be(null)
        results.getStatus mustBe 400
      }
    }

    it("Uses the configured client configuration") {
      val injector = getInjector(List(new ExampleOneClientModule))
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
