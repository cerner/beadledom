.. _avro-swagger:

beadledom-avro-swagger
======================

Produces Swagger model schemas for Avro generated classes. The resulting Swagger model schemas are
produced by inspecting the Avro schema rather than inspecting the Java classes for members and
annotations. The result is automatic model API documentation for responses modeled using Avro files.

For more information about Swagger model schemas `see their documentation <http://swagger.io/specification/#responseObject>`_.

Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom.avro</groupId>
      <artifactId>beadledom-avro-swagger</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Usage
~~~~~

* Install `AvroSwaggerGuiceModule <https://github.com/cerner/beadledom/blob/master/avro/swagger/src/main/java/com/cerner/beadledom/avro/AvroSwaggerGuiceModule.java>`_ to enable the avro serialization support.

.. code-block:: java

  public class ConsumerModule extends AbstractModule {
    @Override
    protected void configure() {
       ...

       install(new AvroSwaggerGuiceModule());

       ...
    }
  }
