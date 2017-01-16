package com.cerner.beadledom.jackson

import com.cerner.beadledom.testing.UnitSpec
import com.fasterxml.jackson.core.JsonParser

/**
  * Spec tests for {@link JsonParserFeatureFlag}.
  */
class JsonParserFeatureFlagSpec extends UnitSpec {
  describe("JsonParserFeatureFlag") {
    it("Creates a JsonParserFeatureFlag object") {
      val jsonParserFeature : JsonParserFeatureFlag = JsonParserFeatureFlag.create(
        JsonParser.Feature.ALLOW_COMMENTS, true)

      jsonParserFeature.feature() must be(JsonParser.Feature.ALLOW_COMMENTS)
      jsonParserFeature.isEnabled mustBe true
    }
  }
}
