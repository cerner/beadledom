.. _beadledom-newrelic-jaxrs:

beadledom-newrelic-jaxrs
========================

Beadledom New Relic JAX-RS is a Google Guice extension for `New Relic`_, adding metadata to New Relic transactions.

 
Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-newrelic-jaxrs</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

NewRelicJaxRsModule_ is used to add a JAXRS request filter that adds the correlation id from the HTTP requests to New Relic transactions.

Install NewRelicModule module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

  public class SomeModule extends BaseModule {
      @Override
      protected void configure() {
          ....
          install(new NewRelicJaxRsModule());
          ....
      }
  }

.. _New Relic: https://newrelic.com/
.. _NewRelicJaxRsModule: https://github.com/cerner/beadledom/blob/master/newrelic-jaxrs/src/main/java/com/cerner/beadledom/newrelic/NewRelicJaxRsModule.java
