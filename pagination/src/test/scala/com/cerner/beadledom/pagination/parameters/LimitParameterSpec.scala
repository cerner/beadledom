package com.cerner.beadledom.pagination.parameters

import com.cerner.beadledom.pagination.OffsetPaginationModule
import com.cerner.beadledom.pagination.models.OffsetPaginationConfiguration
import com.google.inject.multibindings.OptionalBinder
import com.google.inject.{AbstractModule, Guice}
import javax.ws.rs.WebApplicationException
import org.scalatest.{FunSpec, MustMatchers}

/**
 * Spec for LimitParameter.
 *
 * @author John Leacox
 */
class LimitParameterSpec extends FunSpec with MustMatchers {
  describe("LimitParameter") {
    describe("#getValue") {
      it("throws an WebApplicationException if param is 0") {
        val limitParameter = new LimitParameter("0")

        val exception = intercept[WebApplicationException] {
          limitParameter.getValue
        }

        exception.getResponse.getStatus must be
        400
      }

      describe("when a zero limit is allowed") {
        it("parses and returns 0") {
          // Guice must be setup for static injection of the config
          Guice.createInjector(new AbstractModule {
            override def configure(): Unit = {
              val paginationConfig = OffsetPaginationConfiguration.builder()
                  .setAllowZeroLimit(true)
                  .build()
              OptionalBinder.newOptionalBinder(binder(), classOf[OffsetPaginationConfiguration])
                  .setBinding().toInstance(paginationConfig)
              install(new OffsetPaginationModule())
            }
          })

          val limitParameter = new LimitParameter("0")

          limitParameter.getValue mustBe 0
        }
      }
    }
  }
}
