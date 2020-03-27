package com.cerner.beadledom.jaxrs

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec tests for {@link JaxRsParamConditions}.
 *
 * @author John Leacox
 * @author Nimesh Subramanian
 */
class JaxRsParamConditionsSpec extends AnyFunSpec with Matchers {
  describe("JaxRsParamConditions") {
    describe("checkParam") {
      it("must throw WebApplicationException with 400 status when expression is false") {
        val exception = intercept[WebApplicationException] {
          JaxRsParamConditions.checkParam(false, "some message")
        }

        exception.getResponse.getStatus must be(400)
        exception.getResponse.getMediaType must be(MediaType.TEXT_PLAIN_TYPE)
        exception.getMessage must be("some message")
      }

      it("must not throw an exception when expression is true") {
        JaxRsParamConditions.checkParam(true, "some message")
      }
    }
  }
}
