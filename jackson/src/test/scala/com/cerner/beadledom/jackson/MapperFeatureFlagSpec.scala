package com.cerner.beadledom.jackson

import com.cerner.beadledom.testing.UnitSpec
import com.fasterxml.jackson.databind.MapperFeature

/**
  * Spec tests for {@link MapperFeatureFlag}.
  */
class MapperFeatureFlagSpec extends UnitSpec {
  describe("MapperFeatureFlag") {
    it("Creates a MapperFeatureFlag object") {
      val mapperFeature : MapperFeatureFlag = MapperFeatureFlag.create(
        MapperFeature.AUTO_DETECT_CREATORS, true)

      mapperFeature.feature() must be(MapperFeature.AUTO_DETECT_CREATORS)
      mapperFeature.isEnabled mustBe true
    }
  }
}
