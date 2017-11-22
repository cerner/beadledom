beadledom-core
==============

A Guice module that pulls all of the beadledom components together for consumption in a JAX-RS based project. This module is still JAX-RS implementation agnostic. Beadledom also provides the beadledom-resteasy project to pull everything together for use with Resteasy.

The following beadledom guice modules are installed.

* `BeadledomConfigurationModule <configuration>`_
* `JacksonModule <jackson>`_
* `JaxRsModule <jaxrs>`_

Download
--------

Download using Maven:

.. code-block:: xml

  <dependencies>
    ...
    <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-core</artifactId>
      <version>[Insert latest version]</version>
    </dependency>
    ...
  </dependencies>

Usage
-----

Install the guice module as shown below.

.. code-block:: java

  public class ConsumerModule extends AbstractModule {
    @Override
    protected void configure() {
       ...

       install(new BeadledomModule());

       ...
    }
  }
