package com.cerner.beadledom.jaxrs.provider

import java.net.URI

import com.cerner.beadledom.json.common.model.JsonError
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.{MediaType, Response, UriInfo}
import org.jboss.resteasy.specimpl.ResteasyUriInfo
import org.mockito
import org.mockito.{ArgumentCaptor, Mockito}
import org.mockito.ArgumentMatchers.any
import org.scalatest._

import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MalformedRequestFilterSpec
  extends AnyFunSpec with BeforeAndAfterAll with Matchers with MockitoSugar {

  describe("MalformedRequestFilter with a valid URI") {

    val containerRequestContext = Mockito.mock(classOf[ContainerRequestContext])
    val uriInfo = new ResteasyUriInfo("http://localhost:8080", "")
    Mockito.when(containerRequestContext.getUriInfo).thenReturn(uriInfo)

    val malformedRequestFilter = new MalformedRequestFilter()

    it("does not call abortWith") {
      malformedRequestFilter.filter(containerRequestContext)

      Mockito.verify(containerRequestContext).getUriInfo();
      Mockito.verifyNoMoreInteractions(containerRequestContext);
    }

  }
  describe("MalformedRequestFilter with an invalid URI") {

    val containerRequestContext = Mockito.mock(classOf[ContainerRequestContext])
    val uriInfo = new ResteasyUriInfo("http://localhost:8080", "?test=%9")
    Mockito.when(containerRequestContext.getUriInfo).thenReturn(uriInfo)

    val malformedRequestFilter = new MalformedRequestFilter()
    it("calls abortWith") {
      malformedRequestFilter.filter(containerRequestContext)

      Mockito.verify(containerRequestContext)
        .abortWith(any(classOf[Response]))

    }

  }
}
