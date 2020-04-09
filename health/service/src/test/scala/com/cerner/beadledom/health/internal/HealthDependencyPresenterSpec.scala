package com.cerner.beadledom.health.internal

import com.cerner.beadledom.health.dto.{HealthDependencyDto, LinksDto}
import com.cerner.beadledom.health.internal.presenter.HealthDependencyPresenter
import org.scalatest._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec tests for HealthDependencyPresenter.
 *
 * @author John Leacox
 */
class HealthDependencyPresenterSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("HealthDependencyPresenter") {
    val healthDependencyDto = HealthDependencyDto.builder()
        .setName("abra")
        .setHealthy(true)
        .setId("abra")
        .setMessage("abra abra abra")
        .setLinks(LinksDto.builder()
            .setSelf("professor")
            .build())
        .build()
    val healthDependencyPresenter = new HealthDependencyPresenter(healthDependencyDto)

    describe("#HealthDependencyPresenter") {
      it("should throw NullPointerException when Connection instance  is null") {
        intercept[NullPointerException] {
          new HealthDependencyPresenter(null)
        }
      }
    }

    describe("#getName") {
      it("returns the name") {
        healthDependencyPresenter.getName must be ("abra")
      }
    }

    describe("#getMessage") {
      it("returns the message") {
        healthDependencyPresenter.getMessage.get() must be ("abra abra abra")
      }
    }

    describe("#getLink") {
      it("returns the link") {
        healthDependencyPresenter.getLink.get() must be ("professor")
      }
    }

    describe("#getStatusClass") {
      it("returns the healthy CSS class") {
        healthDependencyPresenter.getStatusClass must be ("healthy")
      }

      it("returns the unhealthy CSS class") {
        val healthDependencyDto = HealthDependencyDto.builder()
            .setHealthy(false)
            .setId("abra")
            .build()
        val healthDependencyPresenter = new HealthDependencyPresenter(healthDependencyDto)
        healthDependencyPresenter.getStatusClass must be ("unhealthy")
      }
    }

    describe("#getStatusText") {
      it("returns the Available status") {
        healthDependencyPresenter.getStatusText must be("Available")
      }

      it("returns the Unavailable status") {
        val healthDependencyDto = HealthDependencyDto.builder()
            .setHealthy(false)
            .setId("abra")
            .build()
        val healthDependencyPresenter = new HealthDependencyPresenter(healthDependencyDto)
        healthDependencyPresenter.getStatusText must be ("Unavailable")
      }
    }
  }
}
