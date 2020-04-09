package com.cerner.beadledom.configuration

import javax.naming.{Context, InitialContext}
import org.apache.commons.configuration2.Configuration
import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Specs for [[JndiConfigurationSource]].
 */
class JndiConfigurationSourceSpec extends AnyFunSpec with BeforeAndAfter with Matchers {

  System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
    "org.apache.naming.java.javaURLContextFactory")
  System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming")

  val initialContext = new InitialContext

  var jndiConfigurationSource: JndiConfigurationSource = _

  before {
    initialContext.createSubcontext("java:comp")
    initialContext.createSubcontext("java:comp/env")
    initialContext.createSubcontext("java:comp/env/test")

    initialContext.bind("java:comp/env/test/chewy", "spree")
    initialContext.bind("java:comp/env/test/gummy", "bears")

    jndiConfigurationSource = JndiConfigurationSource
        .create(new InitialContext().lookup("java:comp/env/test").asInstanceOf[Context], 1)
  }

  after {
    initialContext.unbind("java:comp")
  }

  describe("JndiConfigurationSource") {
    it("throws NPE when Context is null") {
      intercept[NullPointerException] {
        JndiConfigurationSource.create(null)
      }
    }

    it("throws IllegalArgumentException if priority is a negative value") {
      intercept[IllegalArgumentException] {
        JndiConfigurationSource.create(initialContext, -1)
      }
    }

    it("sets the priority to default value when priority is not set explicitly") {
      JndiConfigurationSource.create(initialContext).getPriority must
          be(JndiConfigurationSource.DEFAULT_PRIORITY)
    }

    it("loads the configuration only from the given prefix") {
      initialContext.createSubcontext("java:comp/env/test2")
      initialContext.bind("java:comp/env/test2/key", "value")

      val config = JndiConfigurationSource
          .create(new InitialContext().lookup("java:comp/env").asInstanceOf[Context], "test2")
          .getConfig

      val iterator = config.getKeys
      val key = iterator.next()
      iterator.hasNext must be(false)

      key must be("key")
      config.getString(key) must be("value")
    }

    it("loads the everything from the given context when the prefix is not supplied") {
      val config = JndiConfigurationSource
          .create(new InitialContext().lookup("java:comp/env").asInstanceOf[Context])
          .getConfig

      config.getString("test.chewy") must be("spree")
      config.getString("test.gummy") must be("bears")
    }

    it("returns Configuration") {
      val config = jndiConfigurationSource.getConfig

      config.isInstanceOf[Configuration] must be(true)

      config.getString("chewy") must be("spree")
      config.getString("gummy") must be("bears")
    }

    it("returns the priority") {
      jndiConfigurationSource.getPriority must be(1)
    }
  }
}
