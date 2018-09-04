package com.cerner.beadledom.client

import com.cerner.beadledom.client.example.client._
import com.cerner.beadledom.client.example.model.{JsonTwo, JsonOne}
import com.cerner.beadledom.client.example.{ResourceOne, PaginatedClientResource, ResourceTwo}
import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.google.inject._

import org.scalatest.{DoNotDiscover, FunSpec, BeforeAndAfter, MustMatchers}

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

    describe("Pagination") {
      it("injects pagination links to the response") {
        val injector = getInjector(List(new ResourceOneModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results : OffsetPaginatedListDto[JsonOne] = paginatedResource.index(0L, 10).body()

        results mustNot be(null)
        results.totalResults() mustBe 1000
        results.firstLink() mustBe s"$baseUri/paginated?offset=0&limit=10"
        results.lastLink() mustBe s"$baseUri/paginated?offset=990&limit=10"
        results.prevLink() mustBe null
        results.nextLink() mustBe s"$baseUri/paginated?offset=10&limit=10"
      }

      it("injects previous link when beyond the first page") {
        val injector = getInjector(List(new ResourceOneModule))

        val paginatedResource = injector.getInstance(classOf[PaginatedClientResource])

        val results : OffsetPaginatedListDto[JsonOne] = paginatedResource.index(1L, 10).body()

        results mustNot be(null)
        results.totalResults() mustBe 1000
        results.firstLink() mustBe s"$baseUri/paginated?offset=0&limit=10"
        results.lastLink() mustBe s"$baseUri/paginated?offset=990&limit=10"
        results.prevLink() mustBe s"$baseUri/paginated?offset=0&limit=10"
        results.nextLink() mustBe s"$baseUri/paginated?offset=11&limit=10"
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
