package com.beadledom.integration.test

import com.cerner.beadledom.integration.api.HelloWorldResource
import com.cerner.beadledom.integration.api.model.HelloWorldDto
import com.cerner.beadledom.integration.client.{BeadledomIntegrationClientConfig, BeadledomIntegrationClientModule}
import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto
import com.google.inject.{AbstractModule, Guice, Injector, Module}
import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net.URL
import java.util.stream.Collectors
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, MustMatchers}

@RunWith(classOf[JUnitRunner])
class ForwardedHeaderFilterSpec extends FunSpec with MustMatchers with MockitoSugar with BeforeAndAfterAll {

  val baseUri = s"https://localhost/beadledom-integration-service"

  private val defaultTrustStore = System.getProperty("javax.net.ssl.trustStore")
  private val defaultTrustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword")

  override def beforeAll(): Unit = {
    System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir") + "/trust.pkcs12")
    System.setProperty("javax.net.ssl.trustStorePassword", "123456")
  }

  override def afterAll(): Unit = {
    System.setProperty("javax.net.ssl.trustStore", defaultTrustStore)
    System.setProperty("javax.net.ssl.trustStorePassword", defaultTrustStorePassword)
  }

  def getInjector(modules: List[Module]): Injector = {
    val module = new AbstractModule() {
      override def configure(): Unit = {
        modules.foreach(m => install(m))

        bind(classOf[BeadledomIntegrationClientConfig]).toInstance(new BeadledomIntegrationClientConfig(baseUri))
      }
    }
    Guice.createInjector(module)
  }

  it("converts the links for a paginated resource to https") {
    val injector = getInjector(List(new BeadledomIntegrationClientModule))

    val helloWorldResource = injector.getInstance(classOf[HelloWorldResource])

    val result : OffsetPaginatedListDto[HelloWorldDto] = helloWorldResource.getHelloWorld.body()

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
