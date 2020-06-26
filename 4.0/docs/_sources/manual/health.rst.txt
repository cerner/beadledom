beadledom-health
================

This project provides an api for health checks of the service built with beadledom. It adds the
following health check endpoints.

+------------------------------------------------------------+-----------------------------------+
| End point                                                  | Description                       |
+============================================================+===================================+
| ``/meta/availability``                                     | Basic availability check          |
+------------------------------------------------------------+-----------------------------------+
| ``/meta/health/``                                          | Primary health check              |
+------------------------------------------------------------+-----------------------------------+
| ``/meta/health/diagnostic``                                | Diagnostic health check           |
+------------------------------------------------------------+-----------------------------------+
| ``/meta/health/diagnostic/dependencies``                   | Dependency listing                |
+------------------------------------------------------------+-----------------------------------+
| ``/meta/health/diagnostic/dependencies/{dependency_name}`` | Availability check for dependency |
+------------------------------------------------------------+-----------------------------------+
| ``/meta/version``                                          | Application version information   |
+------------------------------------------------------------+-----------------------------------+

To enable the health check endpoints install the HealthModule. By default, all the healthchecks returns 200 status.

Download
--------

Download using Maven:

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-health</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Usage
-----

* With the module installed, you can add new health dependencies to your service's health check by adding bindings of HealthDependency using the Multibinder.

.. code-block:: java

  public class MyModule extends AbstractModule {
    @Override
    public void configure() {
      install(new HealthModule());
    }

    @ProvidesIntoSet
    HealthDependency provideMyHealthDependency() {
      return new FoobarHealthDependency();
    }
  }

  class FoobarHealthDependency extends HealthDependency {
    @Override
    public HealthStatus checkAvailability() {
      if (...) { // check health somehow
        return HealthStatus.create(200, "foobar is available");
      }
      else {
        return HealthStatus.create(503, "foobar is gone. just gone.");
      }
    }

    @Override
    public String getName() {
      return "foobar";
    }
  }

* If your health dependency is a HTTP service, you can use the HttpHealthDependency_ class to declare your dependency.

.. code-block:: java

  public class MyModule extends AbstractModule {
    @Override
    public void configure() {
      install(new HealthModule());
    }

    @ProvidesIntoSet
    HealthDependency provideMyHealthDependency() {
      return new HttpHealthDependency("https://base-url/meta/availability", "dependencyName", isPrimary);
    }
  }

.. _HttpHealthDependency: https://github.com/cerner/beadledom/blob/main/health/service/src/main/java/com/cerner/beadledom/health/HealthDependency.java
