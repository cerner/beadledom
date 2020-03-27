package com.cerner.beadledom.metadata

import java.net.InetAddress
import java.time.Instant
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Spec tests for {@link ServiceMetadata}.
 */
class ServiceMetadataSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("ServiceMetadata") {
    describe(".create") {
      it("builds a default object correctly") {
        val lowerTimeBound = Instant.now().minusMillis(1L)
        val mockBuildInfo = mock[BuildInfo]
        val metadata = ServiceMetadata.create(mockBuildInfo)
        val upperTimeBound = Instant.now().plusMillis(1L)
        metadata.getBuildInfo should be(mockBuildInfo)
        metadata.getHostName.get() should be(InetAddress.getLocalHost.getHostName)
        metadata.getStartupTime.isBefore(upperTimeBound) should be(true)
        metadata.getStartupTime.isAfter(lowerTimeBound) should be(true)
      }
    }
  }

  describe("ServiceMetadata.Builder") {
    it("builds an object correctly") {
      val mockBuildInfo = mock[BuildInfo]
      val metadata = ServiceMetadata.builder()
          .setBuildInfo(mockBuildInfo)
          .setHostName("flake001")
          .setStartupTime(Instant.parse("2001-02-03T04:05:06Z"))
          .build()
      metadata.getBuildInfo should be(mockBuildInfo)
      metadata.getHostName.get() should be("flake001")
      metadata.getStartupTime should be(Instant.parse("2001-02-03T04:05:06Z"))
    }
  }
}
