.. toctree::
   :name: introduction
   :includehidden:

Beadledom
=========

A simple, composable framework for building `RESTful <https://en.wikipedia.org/wiki/Representational_state_transfer>`_ services.

Beadledom takes many best in class components from the Java ecosystem and composes them together
in a lightweight and modular way to enable consumers to quickly and easily build robust ReSTful services.

Providing a single entry point into a service as well as a consistent way of bootstrapping components to make
it is easy for consumers to configure existing and/or install additional functionality. Beadledom's bootstrapping
and configuration relies heavily on `Google Guice <https://github.com/google/guice/wiki/Motivation>`_ so if
you aren't familiar with Guice it is highly recommended you spend a few minutes and read over their introductory
documentation.

Major Components / Libraries
----------------------------

Google Guice:
  Used as the glue and bootstrapping components

Jackson:
  JSON serialization/deserialization

JAX-RS:
  The Java standard used for implementing RESTFul services

RESTEasy:
  JAX-RS implementation used

Stagemonitor:
  Monitoring and metrics

Swagger:
  API Documentation

Additional Reading
------------------

* `Google Guice <https://github.com/google/guice/wiki/Motivation>`_
* `FasterXML Jaxkson <http://wiki.fasterxml.com/JacksonHome>`_
* `JAX-RS 2.0 JavaDoc <https://jax-rs-spec.java.net/nonav/2.0-rev-a/apidocs/index.html>`_
* `JAX-RS 2.0 Spec <https://java.net/projects/jax-rs-spec/pages/Jaxrs20>`_
* `JBoss RESTEasy <http://resteasy.jboss.org/>`_
* `Netflix Governator <https://github.com/Netflix/governator/wiki>`_
* `Stagemonitor <http://www.stagemonitor.org/>`_
* `Swagger <https://github.com/swagger-api/swagger-core/wiki/Annotations>`_

Java Requirements
-----------------
* All components require **Java 8+**.
