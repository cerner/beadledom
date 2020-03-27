package com.cerner.beadledom.resteasy

import com.cerner.beadledom.configuration._
import com.cerner.beadledom.resteasy.fauxservice.FauxContextListener
import com.google.inject.{AbstractModule, Injector, Module, Singleton}
import java.io.FileReader
import java.util
import javax.annotation.{PostConstruct, PreDestroy}
import javax.inject.Inject
import javax.naming.{Context, InitialContext}
import javax.servlet.{ServletContext, ServletContextEvent}
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration
import org.jboss.resteasy.spi.{Registry, ResteasyDeployment, ResteasyProviderFactory}
import org.mockito.Mockito._
import org.scalatest._
import scala.collection.JavaConverters._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec to test the BaseContextListener.
 */
class BaseContextListenerSpec
    extends AnyFunSpec with MockitoSugar with BeforeAndAfter with Matchers {

  val context = mock[ServletContext]
  val deployment: ResteasyDeployment = mock[ResteasyDeployment]
  val registry: Registry = mock[Registry]
  val providerFactory = mock[ResteasyProviderFactory]
  val initializedEvent = mock[ServletContextEvent]

  var initialContext: InitialContext = _

  before {
    initialContext = new InitialContext
    initialContext.createSubcontext("test")

    when(context.getAttribute(classOf[ResteasyDeployment].getName))
        .thenReturn(deployment, deployment)
    when(deployment.getRegistry).thenReturn(registry)
    when(deployment.getProviderFactory).thenReturn(providerFactory)
    when(initializedEvent.getServletContext).thenReturn(context)
  }

  after {
    initialContext.unbind("test")
  }

  describe("BaseContextListener") {
    describe("LifeCycle") {
      it("executes eager singleton startup hooks during context initialized") {
        val testModule: Module = new AbstractModule() {
          override def configure() {
            bind(classOf[InjectedType])
            bind(classOf[EagerSingletonLifecycleHook]).asEagerSingleton()
          }
        }

        val contextListenerUtil = new ContextListeners(List(testModule))
        val contextListener = contextListenerUtil.getContextListener
        contextListener.contextInitialized(initializedEvent)
        val lifecycleHolder = contextListenerUtil.injector
            .getInstance(classOf[SingletonLifecycleHolder])

        lifecycleHolder.hasExecutedStartup must be(true)
        lifecycleHolder.hasExecutedShutdown must be(false)
      }

      it("executes eager singleton shutdown hooks during context destroyed") {
        val testModule: Module = new AbstractModule() {
          override def configure() {
            bind(classOf[EagerSingletonLifecycleHook]).asEagerSingleton()
          }
        }

        val contextListenerUtil = new ContextListeners(List(testModule))
        val contextListener = contextListenerUtil.getContextListener

        contextListener.contextInitialized(initializedEvent)

        val destroyedEvent = mock[ServletContextEvent]
        contextListener.contextDestroyed(destroyedEvent)

        val lifecycleHolder = contextListenerUtil.injector
            .getInstance(classOf[SingletonLifecycleHolder])

        lifecycleHolder.hasExecutedStartup must be(true)
        lifecycleHolder.hasExecutedShutdown must be(true)
      }

      it("executes startup hooks during context initialized") {
        val testModule: Module = new AbstractModule() {
          override def configure() {
            bind(classOf[InjectedType])
          }
        }

        val contextListenerUtil = new ContextListeners(List(testModule))

        val contextListener = contextListenerUtil.getContextListener

        contextListener.contextInitialized(initializedEvent)

        val lifecycleHolder = contextListenerUtil.injector
            .getInstance(classOf[InjectedTypeLifecycleHolder])

        // must only execute lifecycle hooks after it has been injected
        lifecycleHolder.hasExecutedStartup must be(false)
        lifecycleHolder.hasExecutedShutdown must be(false)

        contextListenerUtil.injector.getInstance(classOf[InjectedType])

        lifecycleHolder.hasExecutedStartup must be(true)
        lifecycleHolder.hasExecutedShutdown must be(false)
      }

      it("executes shutdown hooks during context destroyed") {
        val testModule: Module = new AbstractModule() {
          override def configure() {
            bind(classOf[InjectedType])
          }
        }

        val contextListenerUtil = new ContextListeners(List(testModule))

        val contextListener = contextListenerUtil.getContextListener

        contextListener.contextInitialized(initializedEvent)

        val lifecycleHolder = contextListenerUtil.injector
            .getInstance(classOf[InjectedTypeLifecycleHolder])

        // must only execute lifecycle hooks after it has been injected
        lifecycleHolder.hasExecutedStartup must be(false)
        lifecycleHolder.hasExecutedShutdown must be(false)

        contextListenerUtil.injector.getInstance(classOf[InjectedType])

        val destroyedEvent = mock[ServletContextEvent]
        contextListener.contextDestroyed(destroyedEvent)

        lifecycleHolder.hasExecutedStartup must be(true)
        lifecycleHolder.hasExecutedShutdown must be(true)
      }
    }

    describe("Configuration") {
      it("loads all the configuration from different sources") {
        initialContext.bind("test/key1", "value1")
        initialContext.bind("test/key2", "123")

        val ic = initialContext.lookup("test").asInstanceOf[Context]
        val propertiesReader = new FileReader(
          classOf[FauxContextListener].getResource("build-info.properties").getPath)
        val xmlReader = new FileReader(
          classOf[FauxContextListener].getResource("test-config.xml").getPath)

        val module = new AbstractModule {
          override def configure(): Unit = {
            install(ConfigurationSourcesModuleBuilder.newBuilder
                .addSource(PropertiesConfigurationSource.create(propertiesReader))
                .addSource(JndiConfigurationSource.create(ic))
                .addSource(XmlConfigurationSource.create(xmlReader))
                .build())
            install(new BeadledomConfigurationModule)
          }
        }

        val contextListenerUtil = new ContextListeners(List(module))

        contextListenerUtil.getContextListener.contextInitialized(initializedEvent)

        val config = contextListenerUtil.injector
            .getInstance(classOf[ImmutableHierarchicalConfiguration])

        config.getString("git.commit.id") must be("abcde")
        config.getString("project.artifactId") must be("faux-service")
        config.getString("project.groupId") must be("com.phony")
        config.getString("project.version") must be("0.0.1-alpha")
        config.getString("project.build.date") must be("2016-07-29T06:12:33-05:00")

        config.getString("key1") must be("value1")
        config.getInt("key2") must be(123)

        config.getString("superman") must be("Clark Kent")
        config.getString("batman") must be("Bruce Wayne")
        config.getString("flash") must be("Barry Allen")
      }

      it("property from the highest priority configuration source takes precedence") {
        initialContext.bind("test/batman", "brooce vain")

        val reader = new FileReader(
          classOf[FauxContextListener].getResource("test-config.xml").getPath)

        val module = new AbstractModule {
          override def configure(): Unit = {
            install(ConfigurationSourcesModuleBuilder.newBuilder
                .addSource(XmlConfigurationSource.create(reader, 200))
                .addSource(JndiConfigurationSource
                    .create(initialContext.lookup("test").asInstanceOf[Context], 300))
                .build())
            install(new BeadledomConfigurationModule)
          }
        }
        val contextListener = new ContextListeners(List(module))

        contextListener.getContextListener.contextInitialized(initializedEvent)

        val config = contextListener.injector
            .getInstance(classOf[ImmutableHierarchicalConfiguration])

        config.getString("batman") must be("brooce vain")
      }
    }
  }
}

@Singleton
class SingletonLifecycleHolder {
  var hasExecutedStartup = false
  var hasExecutedShutdown = false
}

class EagerSingletonLifecycleHook @Inject()(val holder: SingletonLifecycleHolder) {
  @PostConstruct
  def startup() {
    holder.hasExecutedStartup = true
  }

  @PreDestroy
  def shutdown() {
    holder.hasExecutedShutdown = true
  }
}

@Singleton
class InjectedTypeLifecycleHolder {
  var hasExecutedStartup = false
  var hasExecutedShutdown = false
}

class InjectedType @Inject()(val holder: InjectedTypeLifecycleHolder) {
  @PostConstruct
  def startup() {
    holder.hasExecutedStartup = true
  }

  @PreDestroy
  def shutdown() {
    holder.hasExecutedShutdown = true
  }
}

private class ContextListeners(modules: List[_ <: Module]) {
  private[this] val _contextListener = new ResteasyContextListener {
    override protected def getModules(
        context: ServletContext): util.List[_ <: Module] = modules.asJava
    override protected def withInjector(injector1: Injector): Unit = {
      _injector = injector1
    }
  }
  private[this] var _injector: Injector = _
  def getContextListener = _contextListener
  def injector = _injector
}
