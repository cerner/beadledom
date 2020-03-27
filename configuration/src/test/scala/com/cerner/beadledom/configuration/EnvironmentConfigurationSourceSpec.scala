package com.cerner.beadledom.configuration

import org.apache.commons.configuration2.{Configuration, EnvironmentConfiguration}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Specs for [[EnvironmentConfigurationSource]].
  */
class EnvironmentConfigurationSourceSpec
    extends AnyFunSpec with BeforeAndAfterAll with Matchers {

  describe("EnvironmentConfigurationSource") {

    it("throws NPE when configuration is null") {
      intercept[NullPointerException] {
        EnvironmentConfigurationSource.create(null.asInstanceOf[EnvironmentConfiguration], 100)
      }
    }

    it("throws IllegalArgumentException if priority is a negative value") {
      intercept[IllegalArgumentException] {
        EnvironmentConfigurationSource.create(-1)
      }
    }

    it("returns the Configuration") {
      val config = EnvironmentConfigurationSource.create().getConfig
      config.isInstanceOf[Configuration] must be(true)
      config.getProperty("PATH") must be(System.getenv("PATH"))
    }

    it("returns the priority") {
      EnvironmentConfigurationSource.create().getPriority must
          be(EnvironmentConfigurationSource.DEFAULT_PRIORITY)
    }
  }
}
