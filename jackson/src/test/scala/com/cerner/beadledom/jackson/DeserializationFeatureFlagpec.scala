package com.cerner.beadledom.jackson

import com.cerner.beadledom.testing.UnitSpec
import com.fasterxml.jackson.databind.DeserializationFeature

/**
  * Spec tests for {@link DeserializationFeatureFlag}.
  */
class DeserializationFeatureFlagpec extends UnitSpec {
  describe("DeserializationFeatureFlag") {
    it("Creates a DeserializationFeatureFlag object") {
      val deserializationFeature : DeserializationFeatureFlag = DeserializationFeatureFlag.create(
        DeserializationFeature.WRAP_EXCEPTIONS, true)

      deserializationFeature.feature() must be(DeserializationFeature.WRAP_EXCEPTIONS)
      deserializationFeature.isEnabled mustBe true
    }
  }
}
