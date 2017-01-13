package com.cerner.beadledom.configuration

import java.io.{File, FileReader, Reader}
import org.apache.commons.configuration2.Configuration
import org.scalatest.{BeforeAndAfter, FunSpec, MustMatchers}
import scala.xml.XML

/**
 * Specs for [[XmlConfigurationSource]].
 */
class XmlConfigurationSourceSpec extends FunSpec with BeforeAndAfter with MustMatchers {
  var reader: Reader = _

  before {
    reader = new FileReader(getClass.getClassLoader.getResource("test-config.xml").getPath)
  }

  after {
    reader.close()
  }

  describe("XmlConfigurationSource") {
    it("throws NPE when path is null") {
      intercept[NullPointerException] {
        XmlConfigurationSource.create(null)
      }
    }

    it("throws IllegalArgumentException if priority is a negative value") {
      intercept[IllegalArgumentException] {
        XmlConfigurationSource.create(reader, -1)
      }
    }

    it("returns the Configuration") {
      val config = XmlConfigurationSource.create(reader).getConfig
      config.isInstanceOf[Configuration] must be(true)

      config.getString("name") must be("Lays")
      config.getString("type") must be("Potato Chips")

      val list = config.getList("flavors.flavor")
      list.size must be(2)

      list.get(0) must be("Barbecue")
      list.get(1) must be("Onion Cream")
    }

    it("returns the priority") {
      XmlConfigurationSource.create(reader).getPriority must
          be(XmlConfigurationSource.DEFAULT_PRIORITY)
    }

    it("loads the configuration even if the underlying file is deleted") {
      val xmlContent = <super-heroes>
        <batman>Bruce Wayne</batman>
      </super-heroes>

      XML.save("super-heroes.xml", xmlContent, "UTF-8", true, null)

      val source = XmlConfigurationSource.create(new FileReader("super-heroes.xml"))
      new File("super-heroes.xml").delete must be(true)

      source.getConfig.getString("batman") must be("Bruce Wayne")
    }
  }
}
