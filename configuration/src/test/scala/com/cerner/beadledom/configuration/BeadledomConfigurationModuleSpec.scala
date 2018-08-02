package com.cerner.beadledom.configuration

import com.google.inject._
import com.google.inject.multibindings.ProvidesIntoSet
import java.io.FileReader
import javax.naming.{Context, InitialContext}
import org.apache.commons.configuration2.ex.ConfigurationException
import org.apache.commons.configuration2.{CombinedConfiguration, Configuration, ImmutableHierarchicalConfiguration}
import org.mockito.Mockito.{mock => _}
import org.scalatest.{BeforeAndAfter, FunSpec, MustMatchers}
import scala.collection.JavaConverters._
import uk.org.lidalia.slf4jext.Level
import uk.org.lidalia.slf4jtest.TestLoggerFactory

/**
 * Specs for [[BeadledomConfigurationModule]].
 */
class BeadledomConfigurationModuleSpec extends FunSpec with BeforeAndAfter with MustMatchers {

  System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
    "org.apache.naming.java.javaURLContextFactory")
  System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming")

  val commonValuesMap = Map(
    "initialContext" -> "value from initial context",
    "properties" -> "value from properties file",
    "xml" -> "value from xml file"
  )
  val initialContext = new InitialContext
  var propertiesReader: FileReader = _
  var xmlReader: FileReader = _

  before {
    propertiesReader = new FileReader(
      getClass.getClassLoader.getResource("test.properties").getPath)

    xmlReader = new FileReader(
      getClass.getClassLoader.getResource("test-config.xml").getPath)

    initialContext.createSubcontext("java:comp")
    initialContext.createSubcontext("java:comp/env")

    initialContext.createSubcontext("java:comp/env/test")
    initialContext.bind("java:comp/env/test/chex", "mix")
    initialContext.bind("java:comp/env/test/cheetos", "puffs")

    initialContext.createSubcontext("java:comp/env/common")
    initialContext.bind("java:comp/env/common/key", commonValuesMap("initialContext"))
  }

  after {
    propertiesReader.close()
    xmlReader.close()
    initialContext.unbind("java:comp")
  }

  describe("BeadledomConfigurationModule") {
    it("fails to create configuration if ConfigurationException occurs") {
      val module = new AbstractModule {
        override def configure(): Unit = {
          install(new BeadledomConfigurationModule)
        }

        @ProvidesIntoSet def providesConfig: ConfigurationSource = {
          new AbstractConfigurationSource() {
            override def getConfig: Configuration = throw new ConfigurationException
            override def getPriority: Int = 1
          }
        }
      }

      val injector = Guice.createInjector(module)

      intercept[ProvisionException] {
        injector.getInstance(classOf[ImmutableHierarchicalConfiguration])
      }
    }

    it("provides an empty set of ConfigurationSources by default if none are provided") {
      val injector = Guice.createInjector(new BeadledomConfigurationModule)

      val configurationSources: java.util.Set[ConfigurationSource] = injector
          .getInstance(Key.get(new TypeLiteral[java.util.Set[ConfigurationSource]]() {}))

      configurationSources.isEmpty must be(true)
    }

    it("loads the configuration based on default priority") {
      val injector = Guice
          .createInjector(
            ConfigurationSourcesModuleBuilder.newBuilder
                .addSource(JndiConfigurationSource
                    .create(new InitialContext().lookup("java:comp/env").asInstanceOf[Context]))
                .addSource(PropertiesConfigurationSource.create(propertiesReader))
                .addSource(XmlConfigurationSource.create(xmlReader))
                .build(),
            new BeadledomConfigurationModule
          )

      val config = injector.getInstance(classOf[ImmutableHierarchicalConfiguration])

      config.getString("common.key") must be(commonValuesMap("initialContext"))

      config.getString("test.chex") must be("mix")
      config.getString("test.cheetos") must be("puffs")

      config.getString("chewy") must be("spree")
      config.getString("gummy") must be("bears")

      config.getString("name") must be("Lays")
      config.getString("type") must be("Potato Chips")

      val list = config.getList("flavors.flavor")
      list.size must be(2)

      list.get(0) must be("Barbecue")
      list.get(1) must be("Onion Cream")
    }

    it("loads the configuration as per the priority across different configuration") {
      val injector = Guice
          .createInjector(
            ConfigurationSourcesModuleBuilder.newBuilder
                .addSource(JndiConfigurationSource
                    .create(new InitialContext().lookup("java:comp/env").asInstanceOf[Context], 1))
                .addSource(PropertiesConfigurationSource.create(propertiesReader, 2))
                .addSource(XmlConfigurationSource.create(xmlReader, 3))
                .build(),
            new BeadledomConfigurationModule
          )
      val config = injector.getInstance(classOf[ImmutableHierarchicalConfiguration])
      config.getString("common.key") must be(commonValuesMap("xml"))
    }

    it("logs a warning when configuration sources have same priority") {
      val logger = TestLoggerFactory.getTestLogger(classOf[BeadledomConfigurationModule])
      val module1 = new AbstractModule {
        override def configure(): Unit = {
          install(ConfigurationSourcesModuleBuilder.newBuilder
              .addSource(PropertiesConfigurationSource.create(propertiesReader, 1))
              .build())
        }
      }

      val module2 = new AbstractModule {
        override def configure(): Unit = {
          install(ConfigurationSourcesModuleBuilder.newBuilder
              .addSource(JndiConfigurationSource
                  .create(new InitialContext().lookup("java:comp/env").asInstanceOf[Context], null,
                    1))
              .build())
        }
      }

      val injector = Guice.createInjector(
        module1,
        module2,
        ConfigurationSourcesModuleBuilder.newBuilder
            .addSource(XmlConfigurationSource.create(xmlReader, 1))
            .build(),
        new BeadledomConfigurationModule
      )

      injector.getInstance(classOf[ImmutableHierarchicalConfiguration])

      val logs = logger.getAllLoggingEvents.asList.asScala

      logs.size must be(2)
      logs.foreach(log => log.getLevel must be(Level.WARN))
    }
  }
}
