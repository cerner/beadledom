package com.cerner.beadledom.jackson

import com.cerner.beadledom.testing.UnitSpec
import com.fasterxml.jackson.core.JsonGenerator

/**
  * Spec tests for {@link JsonGeneratorFeatureFlag}.
  */
class JsonGeneratorFeatureFlagSpec extends UnitSpec {
  describe("JsonGeneratorFeatureFlag") {
    it("Creates a JsonGeneratorFeatureFlag object") {
      val jsonGeneratorFeature : JsonGeneratorFeatureFlag = JsonGeneratorFeatureFlag.create(
        JsonGenerator.Feature.AUTO_CLOSE_TARGET, true)

      jsonGeneratorFeature.feature() must be(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
      jsonGeneratorFeature.isEnabled mustBe true
    }
  }
}
