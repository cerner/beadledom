beadledom-resteasy
==================

This project provides the core integration of the Beadledom components with the Resteasy JAX-RS 
implementation.

The following components are included:

`ResteasyContextListener <https://github.com/cerner/beadledom/blob/master/resteasy/src/main/java/com/cerner/beadledom/resteasy/ResteasyContextListener.java>`_
  a base servlet context listener that integrates with Resteasy and Governator for lifecycle management

`ResteasyModule <https://github.com/cerner/beadledom/blob/master/resteasy/src/main/java/com/cerner/beadledom/resteasy/ResteasyModule.java>`_
  a Guice module that provides the core bindings.

Download
--------

 Download using Maven:

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-resteasy</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

Beadledom component integration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Beadledom components are integrated into this project via `beadledom-core <core>`_.

Getting started
~~~~~~~~~~~~~~~

To use Beadledom in your Resteasy/Jax-rs application all that is needed is to extend the 
``ResteasyContextListener`` and include the ``ResteasyModule`` in your own Guice module. Then include
the context listener in the web.xml.


Guice Module:

.. code-block:: java

  public class ExampleModule extends AbstractModule {
    @Override
    protected void configure() {
      install(new ResteasyModule());

      bind(ExampleResource.class);

      BuildInfo buildInfo = BuildInfo.load(getClass().getResourceAsStream("build-info.properties"));
      bind(BuildInfo.class).toInstance(buildInfo);
      bind(ServiceMetadata.class).toInstance(ServiceMetadata.create(buildInfo));

      Multibinder<HealthDependency> healthDependencyBinder =
          Multibinder.newSetBinder(binder(), HealthDependency.class);
      healthDependencyBinder.addBinding().to(ExampleHealthDependency.class);

      bind(ExampleLifecycleHook.class).asEagerSingleton();
    }

    private static class ExampleLifecycleHook {
      @PostConstruct
      public void postConstruct() {
        // Do stuff at startup
      }

      @PreDestroy
      public void preDestroy() {
        // Do stuff at shutdown
      }
    }
  }

Context Listener:

.. code-block:: java

  public class ExampleContextListener extends ResteasyContextListener {
    @Override
    protected List<? extends Module> getModules(ServletContext context) {
      return Lists.newArrayList(new ExampleModule());
    }

    @Override
    protected List<? extends ConfigurationSource> getConfigSources() {
      return Lists.newArrayList(PropertiesConfigurationSource.create("my/properties/file/path"),
          JndiConfigurationSource.create(myInitialContext),
          XmlConfigurationSource.create("my/properties/file/path"));
    }
  }

Web.xml:

.. code-block:: xml

  <web-app metadata-complete="false">
      <display-name>Example Service</display-name>

      <listener>
          <listener-class>
              com.example.ExampleContextListener
          </listener-class>
      </listener>

      <servlet>
          <servlet-name>Resteasy</servlet-name>
          <servlet-class>
              org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher
          </servlet-class>
      </servlet>

      <servlet-mapping>
          <servlet-name>Resteasy</servlet-name>
          <url-pattern>/*</url-pattern>
      </servlet-mapping>
  </web-app>
