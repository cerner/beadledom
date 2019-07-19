package com.beadledom.integration.test

import com.cerner.beadledom.integration.api.HelloWorldResource
import com.cerner.beadledom.integration.api.model.HelloWorldDto
import com.cerner.beadledom.integration.client.{BeadledomIntegrationClientConfig, BeadledomIntegrationClientModule}
import com.google.inject.{AbstractModule, Guice, Injector, Module}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, MustMatchers}

@RunWith(classOf[JUnitRunner])
class ForwardedHeaderFilterSpec extends FunSpec with MustMatchers with MockitoSugar {

  val baseUri = s"https://localhost:443/beadledom-integration-service"

  def getInjector(modules: List[Module]): Injector = {
    val module = new AbstractModule() {
      override def configure(): Unit = {
        modules.foreach(m => install(m))

        bind(classOf[BeadledomIntegrationClientConfig]).toInstance(new BeadledomIntegrationClientConfig(baseUri))
      }
    }
    Guice.createInjector(module)
  }

  it("does the stuffs") {
    import okhttp3.OkHttpClient
    import okhttp3.Request
    val client = RestEasyClientAcceptAllCerts.getUnsafeOkHttpClient
    val request = new Request.Builder().url(baseUri + "/hello").build
    val response = client.newCall(request).execute

    response mustNot be(null)
    response.code() mustBe 200
  }

}
