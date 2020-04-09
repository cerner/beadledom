package com.cerner.beadledom.jaxrs

import com.cerner.beadledom.testing.UnitSpec
import java.lang.annotation.Annotation
import java.net.URI
import java.util.{Date, Locale}
import javax.ws.rs.core.Response.StatusType
import javax.ws.rs.core._
import org.jboss.resteasy.specimpl.MultivaluedMapImpl
import org.mockito.Mockito.{verify, when}
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar

/**
  * Spec tests for [[GenericResponseBuilder]].
  *
  * @author John Leacox
  */
class GenericResponseBuilderSpec extends UnitSpec with MockitoSugar {

  class TestingGenericResponseBuilder[T](rawBuilder: Response.ResponseBuilder)
      extends GenericResponseBuilder[T](rawBuilder) {
    override protected def build(body: T,
        rawResponse: Response): GenericResponse[T] = DelegatingGenericResponse.create(body, rawResponse)
  }

  describe("GenericResponseBuilder") {
    describe("#status(int status)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.status(200) mustBe builder
        verify(rawBuilder).status(200)
      }
    }

    describe("#status(Response.Status status)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.status(Response.Status.OK) mustBe builder
        verify(rawBuilder).status(Response.Status.OK)
      }
    }

    describe("#status(Response.StatusType status)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val okStatus = new StatusType {
          override def getStatusCode: Int = 200

          override def getReasonPhrase: String = "Ok"

          override def getFamily = Response.Status.Family.SUCCESSFUL
        }
        builder.status(okStatus) mustBe builder
        verify(rawBuilder).status(okStatus)
      }
    }

    describe("#entity(T entity)") {
      it ("sets the body and delegates to the underlying builder") {
        val response = mock[Response]
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)
        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.entity("Hello World") mustBe builder
        verify(rawBuilder).entity("Hello World")
        builder.build().body() mustBe "Hello World"
      }

      it("throw an IllegalStateException if errorEntity is already set") {
        val response = mock[Response]
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)
        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.errorEntity("error")
        intercept[IllegalStateException] {
          builder.entity("Hello World")
        }
      }
    }

    describe("#entity(T entity, Annotation[] annotations)") {
      it ("sets the body and delegates to the underlying builder") {
        val response = mock[Response]
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)
        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.entity("Hello World", Array[Annotation]()) mustBe builder
        verify(rawBuilder).entity("Hello World", Array[Annotation]())
        builder.build().body() mustBe "Hello World"
      }

      it("throw an IllegalStateException if errorEntity is already set") {
        val response = mock[Response]
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)
        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.errorEntity("error")
        intercept[IllegalStateException] {
          builder.entity("Hello World", Array[Annotation]())
        }
      }
    }

    describe("#errorEntity(Object entity)") {
      it ("sets the error body and delegates to the underlying builder") {
        val errorEntity = "Hello World"
        val response = mock[Response]
        when(response.readEntity(classOf[String])).thenReturn(errorEntity)
        when(response.getStatus).thenReturn(500)
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)

        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.errorEntity(errorEntity) mustBe builder
        verify(rawBuilder).entity(errorEntity)
        builder.build().errorBody().string() mustBe errorEntity
      }

      it("throw an IllegalStateException if entity is already set") {
        val response = mock[Response]
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)
        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.entity("Hello World")
        intercept[IllegalStateException] {
          builder.errorEntity("error")
        }
      }
    }

    describe("#errorEntity(Object errorEntity, Annotation[] annotations)") {
      it ("sets the error body and delegates to the underlying builder") {
        val errorEntity = "Hello World"
        val response = mock[Response]
        when(response.readEntity(classOf[String])).thenReturn(errorEntity)
        when(response.getStatus).thenReturn(500)
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)

        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.errorEntity(errorEntity, Array[Annotation]()) mustBe builder
        verify(rawBuilder).entity(errorEntity, Array[Annotation]())
        builder.build().errorBody().string() mustBe errorEntity
      }

      it("throw an IllegalStateException if errorEntity is already set") {
        val response = mock[Response]
        val rawBuilder = mock[Response.ResponseBuilder]
        when(rawBuilder.build()).thenReturn(response)
        // Being able to use the entity method after the builder is already created depends on
        // type inference. This is unavoidable unless we want to use a method level type parameter
        // and create a new builder for the new type. This would also complicate things because
        // it would require a way to create the correct implementation of the builder using existing
        // builder values.
        val builder: GenericResponseBuilder[String] = new TestingGenericResponseBuilder(rawBuilder)

        builder.entity("Hello World")
        intercept[IllegalStateException] {
          builder.errorEntity("error", Array[Annotation]())
        }
      }
    }

    describe("#allow(String... methods)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.allow("GET", "PUT") mustBe builder
        verify(rawBuilder).allow("GET", "PUT")
      }
    }

    describe("#allow(Set<String> methods)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val set = Set("GET", "PUT").asJava
        builder.allow(set) mustBe builder
        verify(rawBuilder).allow(set)
      }
    }

    describe("#cacheControl(CacheControl cacheControl)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val cacheControl = mock[CacheControl]
        builder.cacheControl(cacheControl) mustBe builder
        verify(rawBuilder).cacheControl(cacheControl)
      }
    }

    describe("#encoding(String encoding)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.encoding("enc") mustBe builder
        verify(rawBuilder).encoding("enc")
      }
    }

    describe("#header(String name, Object value)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.header("Accept", "application/json") mustBe builder
        verify(rawBuilder).header("Accept", "application/json")
      }
    }

    describe("#replaceAll(MultivaluedMap<String, Object> headers)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val headers = new MultivaluedMapImpl[String, Object]
        builder.replaceAll(headers) mustBe builder
        verify(rawBuilder).replaceAll(headers)
      }
    }

    describe("#language(String language)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.language("english") mustBe builder
        verify(rawBuilder).language("english")
      }
    }

    describe("#language(Locale language)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.language(Locale.ENGLISH) mustBe builder
        verify(rawBuilder).language(Locale.ENGLISH)
      }
    }

    describe("#type(MediaType type)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.`type`(MediaType.APPLICATION_JSON_TYPE) mustBe builder
        verify(rawBuilder).`type`(MediaType.APPLICATION_JSON_TYPE)
      }
    }

    describe("#type(String type)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.`type`("application/json") mustBe builder
        verify(rawBuilder).`type`("application/json")
      }
    }

    describe("#variant(Variant variant)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val variant = mock[Variant]
        builder.variant(variant) mustBe builder
        verify(rawBuilder).variant(variant)
      }
    }

    describe("#contentLocation(URI location)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val location = new URI("/location')")
        builder.contentLocation(location) mustBe builder
        verify(rawBuilder).contentLocation(location)
      }
    }

    describe("#cookie(NewCookie... cookies)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val cookie = new NewCookie("peanut", "butter")
        builder.cookie(cookie) mustBe builder
        verify(rawBuilder).cookie(cookie)
      }
    }

    describe("#expires(Date expires)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val date = new Date()
        builder.expires(date) mustBe builder
        verify(rawBuilder).expires(date)
      }
    }

    describe("#lastModified(Date lastModified)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val date = new Date()
        builder.lastModified(date) mustBe builder
        verify(rawBuilder).lastModified(date)
      }
    }

    describe("#location(URI location)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val location = new URI("/location')")
        builder.location(location) mustBe builder
        verify(rawBuilder).location(location)
      }
    }

    describe("#tag(EntityTag tag)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val entityTag = new EntityTag("etag")
        builder.tag(entityTag) mustBe builder
        verify(rawBuilder).tag(entityTag)
      }
    }

    describe("#tag(String tag)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.tag("etag") mustBe builder
        verify(rawBuilder).tag("etag")
      }
    }

    describe("#variants(Variant... variants)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val variantOne = mock[Variant]
        val variantTwo = mock[Variant]
        builder.variants(variantOne, variantTwo) mustBe builder
        verify(rawBuilder).variants(variantOne, variantTwo)
      }
    }

    describe("#variants(List<Variant> variants)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val variantOne = mock[Variant]
        val variantTwo = mock[Variant]
        val variants = List(variantOne, variantTwo).asJava
        builder.variants(variants) mustBe builder
        verify(rawBuilder).variants(variants)
      }
    }

    describe("#links(Link... links)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val linkOne = mock[Link]
        val linkTwo = mock[Link]
        builder.links(linkOne, linkTwo) mustBe builder
        verify(rawBuilder).links(linkOne, linkTwo)
      }
    }

    describe("#link(URI uri, String rel)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        val uri = new URI("/link")
        builder.link(uri, "rel") mustBe builder
        verify(rawBuilder).link(uri, "rel")
      }
    }

    describe("#link(String uri, String rel)") {
      it ("delegates to the underlying builder") {
        val rawBuilder = mock[Response.ResponseBuilder]
        val builder = new TestingGenericResponseBuilder(rawBuilder)

        builder.link("uri", "rel") mustBe builder
        verify(rawBuilder).link("uri", "rel")
      }
    }
  }
}
