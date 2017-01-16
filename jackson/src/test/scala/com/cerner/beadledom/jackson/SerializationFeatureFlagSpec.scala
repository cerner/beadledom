package com.cerner.beadledom.jackson

import com.cerner.beadledom.testing.UnitSpec
import com.fasterxml.jackson.databind.SerializationFeature

/**
  * Spec tests for {@link com.cerner.beadledom.jackson.SerializationFeatureFlag}.
  */
class SerializationFeatureFlagSpec extends UnitSpec {
  describe("SerializationFeatureFlag") {
    it("Creates a SerializationFeatureFlag object") {
      val serializationFeature : SerializationFeatureFlag = SerializationFeatureFlag.create(
        SerializationFeature.INDENT_OUTPUT, true)

      serializationFeature.feature() must be(SerializationFeature.INDENT_OUTPUT)
      serializationFeature.isEnabled mustBe true
    }
  }
}
