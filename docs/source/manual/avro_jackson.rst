.. _avro-jackson:

beadledom-avro-jackson
======================

Provides Jackson serialization support for `Avro files <https://avro.apache.org/>`_.

The provided deserializer ensures that Jackson deserializes Avro classes by using the generated builder classes
rather than just instantiating the generated value classes. This ensures that any fields not specified in the input
JSON will have their default values (if any) applied, rather than being left null.

Serialization is a bit simpler but we do ensure that the ``Schema`` component of the Avro file is not serialized.

Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom.avro</groupId>
      <artifactId>beadledom-avro-jackson</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Usage
~~~~~

* Install `AvroJacksonGuiceModule <https://github.com/cerner/beadledom/blob/main/avro/jackson/src/main/java/com/cerner/beadledom/avro/AvroJacksonGuiceModule.java>`_
  to enable the avro serialization support.

.. code-block:: java

  public class ConsumerModule extends AbstractModule {
    @Override
    protected void configure() {
       ...

       install(new AvroJacksonGuiceModule());

       ...
    }
  }
