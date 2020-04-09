package com.cerner.beadledom.configuration

import com.google.inject._
import java.io.FileReader
import javax.naming.{Context, InitialContext}
import org.apache.commons.configuration2.CombinedConfiguration
import org.scalatest.BeforeAndAfter
import scala.collection.JavaConverters._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Specs for [[ConfigurationSourcesModuleBuilder]].
 */
class ConfigurationSourceModuleBuilderSpec extends AnyFunSpec with BeforeAndAfter with Matchers {
  System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
    "org.apache.naming.java.javaURLContextFactory")
  System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming")

  val initialContext = new InitialContext
  val configurationSourcesKey = Key.get(new TypeLiteral[java.util.Set[ConfigurationSource]]() {})
  var propertiesReader: FileReader = _

  before {
    propertiesReader = new FileReader(
      getClass.getClassLoader.getResource("test.properties").getPath)

    initialContext.createSubcontext("java:comp")
    initialContext.createSubcontext("java:comp/env")

    initialContext.createSubcontext("java:comp/env/snacks")
    initialContext.bind("java:comp/env/snacks/chewy", "spree")
    initialContext.bind("java:comp/env/snacks/gummy", "bears")

    initialContext.createSubcontext("java:comp/env/heroes")
    initialContext.bind("java:comp/env/heroes/batman", "wayne")
    initialContext.bind("java:comp/env/heroes/superman", "kent")
  }

  after {
    propertiesReader.close()
    initialContext.unbind("java:comp")
  }
  def createGuiceModule(sources: List[ConfigurationSource]): Module = {
    new AbstractModule {
      override def configure(): Unit = {
        val builder = ConfigurationSourcesModuleBuilder.newBuilder
        sources.foreach(source => builder.addSource(source))
        install(builder.build())
      }
    }
  }

  describe("ConfigurationSourceModule") {
    describe("provides a set of ConfigurationSources") {
      describe("from same module") {
        it("when configuration sources are of different type") {
          val jndiSource = JndiConfigurationSource
              .create(initialContext.lookup("java:comp/env/heroes").asInstanceOf[Context])
          val propertiesSources = PropertiesConfigurationSource.create(propertiesReader)

          val injector = Guice
              .createInjector(createGuiceModule(List(jndiSource, propertiesSources)))

          val configSources = injector.getInstance(configurationSourcesKey).asScala

          configSources must not be null
          configSources.size must be(2)

          val config = new CombinedConfiguration

          configSources.foreach(source => config.addConfiguration(source.getConfig))

          config.getString("chewy") must be("spree")
          config.getString("gummy") must be("bears")
          config.getString("batman") must be("wayne")
          config.getString("superman") must be("kent")
        }

        it("when configuration sources are of same type ") {
          val jndiSource1 = JndiConfigurationSource
              .create(initialContext.lookup("java:comp/env/snacks").asInstanceOf[Context])

          val jndiSource2 = JndiConfigurationSource
              .create(initialContext.lookup("java:comp/env/heroes").asInstanceOf[Context])

          val injector = Guice.createInjector(createGuiceModule(List(jndiSource1)),
            createGuiceModule(List(jndiSource2)))

          val configSources = injector.getInstance(configurationSourcesKey).asScala

          configSources must not be null
          configSources.size must be(2)

          val config = new CombinedConfiguration

          configSources.foreach(source => config.addConfiguration(source.getConfig))

          config.getString("chewy") must be("spree")
          config.getString("gummy") must be("bears")
          config.getString("batman") must be("wayne")
          config.getString("superman") must be("kent")
        }
      }

      describe("from different module") {
        it("when the configuration sources are of different type") {
          val jndiSource = JndiConfigurationSource
              .create(initialContext.lookup("java:comp/env/heroes").asInstanceOf[Context])

          val propertiesSources = PropertiesConfigurationSource.create(propertiesReader)

          val injector = Guice.createInjector(createGuiceModule(List(jndiSource)),
            createGuiceModule(List(propertiesSources)))

          val configSources = injector.getInstance(configurationSourcesKey).asScala

          configSources must not be null
          configSources.size must be(2)

          val config = new CombinedConfiguration

          configSources.foreach(source => config.addConfiguration(source.getConfig))

          config.getString("chewy") must be("spree")
          config.getString("gummy") must be("bears")
          config.getString("batman") must be("wayne")
          config.getString("superman") must be("kent")
        }

        it("when the configuration sources are of same type") {
          val jndiSource1 = JndiConfigurationSource
              .create(initialContext.lookup("java:comp/env/snacks").asInstanceOf[Context])

          val jndiSource2 = JndiConfigurationSource
              .create(initialContext.lookup("java:comp/env/heroes").asInstanceOf[Context])

          val injector = Guice.createInjector(createGuiceModule(List(jndiSource1)),
            createGuiceModule(List(jndiSource2)))

          val configSources = injector.getInstance(configurationSourcesKey).asScala

          configSources must not be null
          configSources.size must be(2)

          val config = new CombinedConfiguration

          configSources.foreach(source => config.addConfiguration(source.getConfig))

          config.getString("chewy") must be("spree")
          config.getString("gummy") must be("bears")
          config.getString("batman") must be("wayne")
          config.getString("superman") must be("kent")
        }
      }
    }
  }
}
