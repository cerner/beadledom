.. _beadledom-newrelic:

beadledom-newrelic
==================

Beadledom New Relic is a Google Guice extension for `New Relic`_, adding metadata to New Relic transactions.

 
Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-newrelic</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

NewRelicModule_ is used to add a JAXRS request filter that adds the correlation id from the HTTP requests to New Relic transactions.

Install NewRelicModule module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

  public class SomeModule extends BaseModule {
      @Override
      protected void configure() {
          ....
          install(new NewRelicModule());
          ....
      }
  }

.. _New Relic: https://newrelic.com/
.. _NewRelicModule: https://github.com/cerner/beadledom/blob/master/newrelic/src/main/java/com/cerner/beadledom/newrelic/NewRelicModule.java
