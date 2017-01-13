package com.cerner.beadledom.jaxrs

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import org.scalatest.{FunSpec, ShouldMatchers}

/**
 * Spec tests for {@link JaxRsParamConditions}.
 *
 * @author John Leacox
 */
class JaxRsParamConditionsSpec extends FunSpec with ShouldMatchers {
  describe("JaxRsParamConditions") {
    describe("checkParam") {
      it("should throw WebApplicationException with 400 status when expression is false") {
        val exception = intercept[WebApplicationException] {
          JaxRsParamConditions.checkParam(false, "some message")
        }
        exception.getResponse.getStatus should be(400)
        exception.getResponse.getEntity should be("some message")
        exception.getResponse.getMediaType should be(MediaType.TEXT_PLAIN_TYPE)
      }

      it("should not throw an exception when expression is true") {
        JaxRsParamConditions.checkParam(true, "some message")
      }
    }
  }
}
