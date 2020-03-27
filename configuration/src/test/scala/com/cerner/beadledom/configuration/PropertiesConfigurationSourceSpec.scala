package com.cerner.beadledom.configuration

import java.io._
import org.apache.commons.configuration2.Configuration
import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Specs for [[PropertiesConfigurationSource]].
 */
class PropertiesConfigurationSourceSpec extends AnyFunSpec with BeforeAndAfter with Matchers {

  var reader: Reader = _

  before {
    reader = new FileReader(getClass.getClassLoader.getResource("test.properties").getPath)
  }

  after {
    reader.close()
  }

  describe("PropertiesConfigurationSource") {
    it("throws NPE when reader is null") {
      intercept[NullPointerException] {
        PropertiesConfigurationSource.create(null.asInstanceOf[Reader])
      }
    }

    it("throws IllegalArgumentException if priority is a negative value") {
      intercept[IllegalArgumentException] {
        PropertiesConfigurationSource.create(reader, -1)
      }
    }

    it("returns the Configuration") {
      val config = PropertiesConfigurationSource.create(reader).getConfig

      config.isInstanceOf[Configuration] must be(true)

      config.getString("chewy") must be("spree")
      config.getString("gummy") must be("bears")

      val list = config.getList("snacks")
      list.size must be(4)

      list.get(0) must be("my")
      list.get(1) must be("dummy")
      list.get(2) must be("gummy")
      list.get(3) must be("bears")
    }

    it("returns the priority") {
      PropertiesConfigurationSource.create(reader).getPriority must
          be(PropertiesConfigurationSource.DEFAULT_PRIORITY)
    }

    it("loads the configuration even if the underlying file is deleted") {
      val file = new File("super-heroes.properties")
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write("batman=Bruce Wayne")
      val reader = new FileReader(file.getPath)
      bw.close()

      val source = PropertiesConfigurationSource.create(reader)
      file.delete() must be(true)

      source.getConfig.getString("batman") must be("Bruce Wayne")
    }
  }
}
