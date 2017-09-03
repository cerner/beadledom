package com.cerner.beadledom.client.resteasy

import com.cerner.beadledom.client._
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, PropertyNamingStrategy, SerializationFeature}
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import javax.ws.rs.client.ClientRequestFilter
import org.scalatest._
import org.slf4j.MDC

/**
 * @author John Leacox
 */
@DoNotDiscover
class ResteasyClientSpec(contextRoot: String, servicePort: Int)
    extends FunSpec with MustMatchers with BeforeAndAfter {
  val baseUrl = s"http://localhost:$servicePort$contextRoot"

  after {
    MDC.clear()
  }

  describe("Beadledom Resteasy client") {
    it("creates a resteasy beadledom client") {
      BeadledomClientBuilder.newBuilder().build().getClass mustBe classOf[BeadledomResteasyClient]
    }

    it("creates a webtarget") {
      BeadledomClientBuilder.newBuilder().buildTarget("http://localhost").getClass mustBe
          classOf[BeadledomResteasyWebTarget]
    }

    it("re-uses the service request correlationId when it is present") {
      val captureFilter = new CaptureCorrelationIdFilter()

      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(captureFilter, Array[Class[_]](classOf[ClientRequestFilter]): _*)
          .build()
      val proxy = client.target(s"$baseUrl/")
          .proxy(classOf[TestResource])

      proxy.loopyGetCorrelationId() must be(captureFilter.getCapturedCorrelationId)
    }

    it("falls back to the MDC correlationId when the header is not present") {
      MDC.put(CorrelationIdContext.DEFAULT_HEADER_NAME, "mdcFallbackId")

      val captureFilter = new CaptureCorrelationIdFilter()

      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(captureFilter, Array[Class[_]](classOf[ClientRequestFilter]): _*)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      proxy.echoCorrelationId() mustBe "mdcFallbackId"
    }

    it("deserializes JSON using underscores by default") {
      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(new JacksonJsonProvider(getDefaultBeadledomObjectMapper), 1)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      val jsonModel = proxy.getJson

      jsonModel.getFieldOne mustBe "one"
      jsonModel.getFieldTwo mustBe "two"
    }

    it("gets a response with json") {
      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(new JacksonJsonProvider(getDefaultBeadledomObjectMapper), 1)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      val response = proxy.getResponseJson

      val jsonModel = response.readEntity(classOf[JsonModel])
      jsonModel.getFieldOne mustBe "one"
      jsonModel.getFieldTwo mustBe "two"
    }

    it("gets a generic response with json") {
      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(new JacksonJsonProvider(getDefaultBeadledomObjectMapper), 1)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      val response = proxy.getGenericResponseJson

      val jsonModel = response.body()
      jsonModel.getFieldOne mustBe "one"
      jsonModel.getFieldTwo mustBe "two"
    }

    it("gets a generic response with json from retrying an endpoint that returns a 503") {
      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(new JacksonJsonProvider(getDefaultBeadledomObjectMapper), 1)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      val response = proxy.getGenericResponseJsonWithRetries

      val jsonModel = response.body()
      jsonModel.getFieldOne mustBe "one"
      jsonModel.getFieldTwo mustBe "two"
    }

    it("gets a response with an error") {
      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(new JacksonJsonProvider(getDefaultBeadledomObjectMapper), 1)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      val response = proxy.getResponseError
      response.getStatus mustBe 400
      response.readEntity(classOf[String]) mustBe "An Error Occurred"
    }

    it("gets a generic response with an error") {
      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(new JacksonJsonProvider(getDefaultBeadledomObjectMapper), 1)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      val response = proxy.getGenericResponseError
      response.getStatus mustBe 400
      response.errorBody().string() mustBe "An Error Occurred"
    }

    it("gets a generic response with an error containing json") {
      val client = BeadledomResteasyClientBuilder.newBuilder()
          .register(new JacksonJsonProvider(getDefaultBeadledomObjectMapper), 1)
          .build()

      val proxy = client.target(baseUrl)
          .proxy(classOf[TestResource])

      val response = proxy.getGenericResponseJsonError
      response.getStatus mustBe 500
      response.errorBody().string() mustBe """{"field_one":"one","field_two":"two"}"""
    }
  }

  /**
   * Return a default {@link ObjectMapper} that is configured to match the default server side
   * settings from {@code beadledom-jackson}.
   */
  private def getDefaultBeadledomObjectMapper: ObjectMapper = {
    val objectMapper: ObjectMapper = new ObjectMapper
    objectMapper
        .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    return objectMapper
  }
}
