package com.cerner.beadledom.resteasy

import com.cerner.beadledom.resteasy.fauxservice.health.ImportantThingHealthDependency
import com.cerner.beadledom.testing.JsonErrorMatchers.beBadRequestError
import com.cerner.beadledom.testing.JsonMatchers.equalJson
import com.google.common.base.Charsets
import java.io.File
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import org.apache.commons.io.{FileUtils, IOUtils}
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, MustMatchers}
import org.skyscreamer.jsonassert.{JSONCompare, JSONCompareMode}
import play.api.libs.json.Json

/**
 *
 * NOTE: These tests depend on the current working directory being the module directory. To make them work in IntelliJ,
 * you can edit the run configuration and set the working directory to the string "$MODULE_DIR$".
 *
 * @author John Leacox
 */
class ResteasyServiceSpec(rootUrl: String, tomcatPort: Int)
    extends FunSpec with MockitoSugar with BeforeAndAfterAll with BeforeAndAfter with
        MustMatchers {

  val client = new ResteasyClientBuilder().connectionPoolSize(5).register().build()

  before {
    ImportantThingHealthDependency.healthy = true
    ImportantThingHealthDependency.throwException = null
  }

  describe("Service derived from BaseContextListener") {
    describe("Health") {
      val renderedBaseDir = new File("target/test-rendered")
      FileUtils.forceMkdir(renderedBaseDir)

      def writeHtml(name: String, html: String) =
        FileUtils.writeStringToFile(new File(renderedBaseDir, s"${name}.html"), html)

      describe("/meta/availability") {
        it("returns text/html") {
          val response = client.target(s"$rootUrl/meta/availability").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("basic_availability", html)
          response.getStatus must be(200)
          html must include("faux-service is available")
        }

        it("returns correct json") {
          val response = client.target(s"$rootUrl/meta/availability").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          val expected = Json.obj("message" -> "faux-service is available")
          response.readEntity(classOf[String]) must equalJson(expected)
        }
      }

      describe("/meta/health") {
        it("returns text/html and 200 status") {
          val response = client.target(s"$rootUrl/meta/health").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("primary_health-200", html)
          response.getStatus must be(200)
          html must include("faux-service is available")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2")
        }

        it("returns correct json and 200 status") {
          val response = client.target(s"$rootUrl/meta/health").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          val expected = Json.obj(
            "dependencies" -> Json.arr(
              Json.obj(
                "healthy" -> true,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing"
                ),
                "message" -> "Fortunately, I'm fine.",
                "name" -> "important-thing",
                "id" -> "important-thing"
              ), Json.obj(
                "id" -> "important-thing2",
                "healthy" -> true,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2"
                ),
                "message" -> "Fortunately, I'm fine."
              )
            ),
            "message" -> "faux-service is available"
          )
          response.readEntity(classOf[String]) must equalJson(expected)
        }

        it("returns text/html and 503 status") {
          ImportantThingHealthDependency.healthy = false
          val response = client.target(s"$rootUrl/meta/health").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("primary_health-503", html)
          response.getStatus must be(503)
          html must include("faux-service is unavailable")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2")
        }

        it("returns correct json and 503 status") {
          ImportantThingHealthDependency.healthy = false
          val response = client.target(s"$rootUrl/meta/health").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(503)
          val expected = Json.obj(
            "dependencies" -> Json.arr(
              Json.obj(
                "healthy" -> false,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing"
                ),
                "message" -> "Uh-oh...",
                "name" -> "important-thing",
                "id" -> "important-thing"
              ), Json.obj(
                "id" -> "important-thing2",
                "healthy" -> true,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2"
                ),
                "message" -> "Fortunately, I'm fine."
              )
            ),
            "message" -> "faux-service is unavailable"
          )
          response.readEntity(classOf[String]) must equalJson(expected)
        }

        it("returns text/html and 503 status when an exception is thrown") {
          ImportantThingHealthDependency.throwException = new IllegalStateException("out of pizza")
          val response = client.target(s"$rootUrl/meta/health").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("primary_health-exception", html)
          response.getStatus must be(503)
          html must include("faux-service is unavailable")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2")
        }

        it("returns json with stack trace, and 503 status, when exception is thrown") {
          ImportantThingHealthDependency.throwException = new IllegalStateException("out of pizza")
          val response = client.target(s"$rootUrl/meta/health").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(503)
          response.readEntity(classOf[String]).contains("java.lang.IllegalStateException") mustBe
              true
        }
      }

      describe("/meta/health/diagnostic") {
        def baseDiagnosticJson = Json.obj(
          "server" -> Json.obj(
            "hostName" -> "bogusbox",
            "startupDateTime" -> "2001-01-01T01:01:01Z"
          ),
          "build" -> Json.obj(
            "artifactName" -> "faux-service",
            "version" -> "0.0.1-alpha",
            "buildDateTime" -> "2016-07-29T06:12:33-05:00"
          )
        )

        it("returns text/html and 200 status") {
          val response = client.target(s"$rootUrl/meta/health/diagnostic")
              .request().accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("diagnostic_health-200", html)
          response.getStatus must be(200)
          html must include("faux-service is available")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2")
          html must include("bogusbox")
        }

        it("returns correct json and 200 status") {
          val response = client.target(s"$rootUrl/meta/health/diagnostic").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          val expected = Json.obj(
            "dependencies" -> Json.arr(
              Json.obj(
                "healthy" -> true,
                "primary" -> true,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing"
                ),
                "message" -> "Fortunately, I'm fine.",
                "name" -> "important-thing",
                "id" -> "important-thing"
              ), Json.obj(
                "id" -> "important-thing2",
                "healthy" -> true,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2"
                ),
                "primary" -> true,
                "message" -> "Fortunately, I'm fine."
              )
            ),
            "message" -> "faux-service is available"
          ) ++ baseDiagnosticJson
          response.readEntity(classOf[String]) must equalJson(expected)
        }

        it("returns text/html and 503 status") {
          ImportantThingHealthDependency.healthy = false
          val response = client.target(s"$rootUrl/meta/health/diagnostic")
              .request().accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("diagnostic_health-503", html)
          response.getStatus must be(503)
          html must include("faux-service is unavailable")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2")
        }

        it("returns correct json and 503 status") {
          ImportantThingHealthDependency.healthy = false
          val response = client.target(s"$rootUrl/meta/health/diagnostic").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(503)
          val expected = Json.obj(
            "dependencies" -> Json.arr(
              Json.obj(
                "healthy" -> false,
                "primary" -> true,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing"
                ),
                "message" -> "Uh-oh...",
                "name" -> "important-thing",
                "id" -> "important-thing"
              ), Json.obj(
                "id" -> "important-thing2",
                "healthy" -> true,
                "primary" -> true,
                "links" -> Json.obj(
                  "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2"
                ),
                "message" -> "Fortunately, I'm fine."
              )
            ),
            "message" -> "faux-service is unavailable"
          ) ++ baseDiagnosticJson
          response.readEntity(classOf[String]) must equalJson(expected)
        }

        it("returns text/html and 503 status when exception is thrown") {
          ImportantThingHealthDependency.throwException = new IllegalStateException("out of pizza")
          val response = client.target(s"$rootUrl/meta/health/diagnostic")
              .request().accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("diagnostic_health-exception", html)
          response.getStatus must be(503)
          html must include("faux-service is unavailable")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2")
        }

        it("returns json with stack trace and 503 status when exception is thrown") {
          val exception = new IllegalStateException("out of pizza")
          ImportantThingHealthDependency.throwException = exception
          val response = client.target(s"$rootUrl/meta/health/diagnostic").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(503)
          response.readEntity(classOf[String]).contains("java.lang.IllegalStateException:") mustBe
              true
        }
      }

      describe("/meta/health/diagnostic/dependencies") {
        it("returns text/html") {
          val response = client.target(s"$rootUrl/meta/health/diagnostic/dependencies").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("dependency_listing", html)
          response.getStatus must be(200)
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing")
          html must include(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2")
        }

        it("returns correct json") {
          val response = client.target(s"$rootUrl/meta/health/diagnostic/dependencies").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          val expected = Json.arr(
            Json.obj(
              "links" -> Json.obj(
                "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing"
              ),
              "name" -> "important-thing",
              "id" -> "important-thing"
            ), Json.obj(
              "id" -> "important-thing2",
              "links" -> Json.obj(
                "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing2"
              )
            )
          )
          response.readEntity(classOf[String]) must equalJson(expected)
        }
      }

      describe("/meta/health/diagnostic/dependencies/{name}") {
        it("returns text/html and 200 status") {
          val response = client
              .target(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("dependency_availability-200", html)
          response.getStatus must be(200)
          html must include("Fortunately")
        }

        it("returns correct json and 200 status") {
          val response = client
              .target(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          val expected = Json.obj(
            "links" -> Json.obj(
              "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing"
            ),
            "message" -> "Fortunately, I'm fine.",
            "name" -> "important-thing",
            "id" -> "important-thing"
          )
          response.readEntity(classOf[String]) must equalJson(expected)
        }

        it("returns text/html and non-200 status") {
          ImportantThingHealthDependency.healthy = false
          val response = client
              .target(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("dependency_availability-500", html)
          response.getStatus must be(503)
          html must include("Uh-oh...")
        }

        it("returns correct json and non-200 status") {
          ImportantThingHealthDependency.healthy = false
          val response = client
              .target(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(503)
          val expected = Json.obj(
            "links" -> Json.obj(
              "self" -> s"$rootUrl/meta/health/diagnostic/dependencies/important-thing"
            ),
            "message" -> "Uh-oh...",
            "name" -> "important-thing",
            "id" -> "important-thing"
          )
          response.readEntity(classOf[String]) must equalJson(expected)
        }

        it("returns text/html and 503 status when exception is thrown") {
          ImportantThingHealthDependency.throwException = new IllegalStateException("out of pizza")
          val response = client
              .target(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("dependency_availability-exception", html)
          response.getStatus must be(503)
          html must include(" java.lang.IllegalStateException:")
        }

        it("returns json with stack trace and 503 status when exception is thrown") {
          val exception = new IllegalStateException("out of pizza")
          ImportantThingHealthDependency.throwException = exception
          val response = client
              .target(s"$rootUrl/meta/health/diagnostic/dependencies/important-thing").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(503)
          response.readEntity(classOf[String])
              .contains("java.lang.IllegalStateException:") mustBe true
        }
      }

      describe("/meta/version") {
        it("returns text/html") {
          val response = client.target(s"$rootUrl/meta/version").request()
              .accept(MediaType.TEXT_HTML).get()
          val html = response.readEntity(classOf[String])
          writeHtml("version", html)
          response.getStatus must be(200)
          html must include("faux-service")
          html must include("2016-07-29T06:12:33-05:00")
          html must include("0.0.1-alpha")
        }

        it("returns correct json") {
          val response = client.target(s"$rootUrl/meta/version").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          val expected = Json.obj(
            "artifactName" -> "faux-service",
            "version" -> "0.0.1-alpha",
            "buildDateTime" -> "2016-07-29T06:12:33-05:00"
          )
          response.readEntity(classOf[String]) must equalJson(expected)
        }
      }
    }

    describe("Swagger") {
      describe("/api-docs") {
        it("returns the expected resource listing") {
          val response = client.target(s"$rootUrl/api-docs").request()
              .accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          response.readEntity(classOf[String]) must equalJson(
            IOUtils.toString(getClass.getResource("expected/api-docs.json")))
        }
      }

      describe("/api-docs/health") {
        it("returns accurate documentation") {
          val response = client.target(s"$rootUrl/api-docs/health")
              .request().accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          val responseJson: String = response.readEntity(classOf[String])
          val expectedJson: String = IOUtils
              .toString(getClass.getResource("expected/api-docs-health.json"), Charsets.UTF_8)
              .replace("TOMCAT_PORT", tomcatPort.toString)
          // Use NON_EXTENSIBLE mode because the resource method order isn't guaranteed
          val result = JSONCompare
              .compareJSON(expectedJson, responseJson, JSONCompareMode.NON_EXTENSIBLE)

          assert(!result.failed(), result.getMessage)
        }
      }

      describe("/api-docs/hello") {
        it("returns accurate documentation") {
          val response = client.target(s"$rootUrl/api-docs/hello")
              .request().accept(MediaType.APPLICATION_JSON).get()
          response.getStatus must be(200)
          response.readEntity(classOf[String]) must equalJson(
            IOUtils.toString(getClass.getResource("expected/api-docs-hello.json"))
                .replace("TOMCAT_PORT", tomcatPort.toString))
        }
      }

      describe("/meta/swagger/ui") {
        it("returns HTML") {
          val response = client.target(s"$rootUrl/meta/swagger/ui").request()
              .accept(MediaType.TEXT_HTML).get()
          response.getStatus must be(200)
          response.close()
        }
      }

      describe("/meta/swagger/{asset}") {
        it("returns needed assets") {
          val response = client.target(s"$rootUrl/meta/swagger/images/explorer_icons.png").request()
              .get()
          response.getStatus must be(200)
          response.close()
        }
      }
    }

    describe("Gzip") {
      it("supports gzipped responses") {
        val response = client.target(s"$rootUrl/hello").request()
            .header("Accept-Encoding", "gzip, deflate").get()
        response.readEntity(classOf[String])
        response.getStatus must be(200)
        response.getHeaders.getFirst("Content-Encoding") must be("gzip")
      }
    }

    describe("Avro") {
      it("supports Avro requests and responses") {
        val response = client.target(s"$rootUrl/hello/echo").request(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).post(Entity.json("""{"text":"HI"}"""))
        response.readEntity(classOf[String]) must be("""{"text":"hi"}""")
      }
    }

    describe("Correlation Id") {
      it("Adds the correlation Id to the response") {
        val correlationValue = "test-correlation"
        val response = client.target(s"$rootUrl/hello").request().accept(MediaType.APPLICATION_JSON)
            .header("Correlation-Id", correlationValue).get()
        response.getHeaderString("Correlation-Id") must be(correlationValue)
      }
    }

    describe("Exception mapping") {
      it("returns 400 for unparseable request json") {
        val response = client.target(s"$rootUrl/hello/echo").request(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).post(Entity.json("""{"text":"HI""""))
        response.getStatus must be(400)
        response.getMediaType.toString mustBe MediaType.APPLICATION_JSON
        response.readEntity(classOf[String]) must beBadRequestError("Unable to parse request JSON")
      }

      it("returns 400 for invalid request json") {
        val response = client.target(s"$rootUrl/hello/echo").request(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).post(Entity.json("""{"text":{"yo":3}}"""))
        response.getStatus must be(400)
        response.getMediaType.toString mustBe MediaType.APPLICATION_JSON
        response.readEntity(classOf[String]) must beBadRequestError("Unable to map request JSON")
      }
    }
  }
}
