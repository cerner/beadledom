package com.beadledom.integration.test

import com.cerner.beadledom.integration.api.model.HelloWorldDto
import com.cerner.beadledom.integration.client.{HelloWorldResource, IntegrationClient, IntegrationClientConfig, IntegrationClientModule}
import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto
import com.google.inject.{AbstractModule, Guice, Injector, Module}
import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net.URL
import java.util.stream.Collectors
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, MustMatchers}

/**
 * Integration Tests that make service requests through a self-signed reverse proxy to test the behavior
 * of the `ForwardedHeaderFilter`
 *
 * @author Nick Behrens
 */
@RunWith(classOf[JUnitRunner])
class ForwardedHeaderFilterSpec extends FunSpec with MustMatchers with MockitoSugar with BeforeAndAfterAll {

  val baseUri = s"https://localhost/beadledom-integration-service"

  // Store the default values for the trust store and password
  // so we can set them back to defaults after the test
  private val defaultTrustStore = System.getProperty("javax.net.ssl.trustStore")
  private val defaultTrustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword")

  // Set the trust store to point to the generated pkcs12 file from the reverse-proxy so the
  // self-signed certificate can be trusted by the JVM.
  override def beforeAll(): Unit = {
    System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir") + "/trust.pkcs12")
    System.setProperty("javax.net.ssl.trustStorePassword", "123456")
  }

  override def afterAll(): Unit = {
    System.setProperty("javax.net.ssl.trustStore", defaultTrustStore)
    System.setProperty("javax.net.ssl.trustStorePassword", defaultTrustStorePassword)
  }

  def getBeadledomIntegrationClientInjector(): Injector = {
    val module = new AbstractModule() {
      override def configure(): Unit = {
        install(new IntegrationClientModule)

        bind(classOf[IntegrationClientConfig]).toInstance(new IntegrationClientConfig(baseUri))
      }
    }
    Guice.createInjector(module)
  }

  it("converts the links for a paginated resource to https") {
    val injector = getBeadledomIntegrationClientInjector()

    val client = injector.getInstance(classOf[IntegrationClient])

    val result : OffsetPaginatedListDto[HelloWorldDto] = client.helloWorldResource().getHelloWorld.body()

    result.items().get(0).getHelloWorldMessage mustBe "Hello World!"
    result.items().get(0).getName mustBe "Beadledom"
    result.firstLink() contains "https"
  }

  it("converts the api-docs link on the swagger-ui page to https") {
    val apiDocsUrl = new URL(baseUri + "/meta/swagger/ui")
    val stream: InputStream = apiDocsUrl.openStream()

    val bufferedReader: BufferedReader = new BufferedReader(new InputStreamReader(stream))

    val swaggerUiString = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()))

    bufferedReader.close()

    swaggerUiString contains baseUri + "/api-docs"
  }
}
