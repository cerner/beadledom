.. _beadledom-jackson:

beadledom-jackson
=================

This project provides an ``ObjectMapper`` with some default configuration via the Guice
``JacksonModule``. This module also provides hooks into a Guice multibinder for the Jackson ``Module``.
This multibinder provides the ability to extend the capabilities of the injected ``ObjectMapper`` by
registering all bound Jackson modules to the injected ``ObjectMapper``.

Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-jackson</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Usage
-----

To add your own Jackson modules to the injected ``ObjectMapper`` simply bind them to the module
multibinder:

.. code-block:: java

  public class MyModule extends AbstractModule {
    @Override
    public void configure() {
      install(new JacksonModule());

      Multibinder<Module> jacksonModules = Multibinder.newSetBinder(binder(), Module.class);
      jacksonModules.addBinding().to(MyJacksonModule.class);
    }
  }

Or with Guice 4:

.. code-block:: java

  public class MyModule extends AbstractModule {
    @Override
    public void configure() {
      install(new JacksonModule());
    }

    @ProvidesIntoSet
    Module provideMyJacksonModule() {
      return new MyJacksonModule();
    }
  }

Jackson ObjectMapper Features
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Defaults
++++++++

The following Jackson features are set by default

* Camel case is converted to snake case
* Null values are not included
* Unknown fields do not cause the deserialization to fail
* Dates are **not** serialized as Timestamps
* Dates are serialized as Strings

Enabling/Disabling Jackson ObjectMapper features via Guice
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

``ObjectMapper`` features can be enabled or disabled by adding the features to their respective
Guice multibinders exposed by the ``JacksonModule``. To configure the injected ``ObjectMapper``,
the following classes can be used:

* ``SerializationFeatureFlag`` to configure ObjectMapper's features defined in the `SerializationFeature <https://github.com/FasterXML/jackson-databind/wiki/Serialization%20Features>`_ class
* ``DeserializationFeatureFlag`` to configure ObjectMapper's defined in the `DeserializationFeature <https://github.com/FasterXML/jackson-databind/wiki/Deserialization%20Features>`_ class
* ``MapperFeatureFlag`` to configure ObjectMapper's features defined in the `MapperFeature <https://github.com/FasterXML/jackson-databind/wiki/Mapper%20Features>`_ class
* ``JsonParserFeatureFlag`` to configure ObjectMapper's defined in the `JsonParser.Feature <https://github.com/FasterXML/jackson-core/wiki/JsonParser-Features>`_ class
* ``JsonGeneratorFeatureFlag`` to configure ObjectMapper's defined in the `JsonGenerator.Feature <https://github.com/FasterXML/jackson-core/wiki/JsonGenerator-Features>`_ class

In your Jackson guice ``module`` add your feature object to the multibinder:

.. code-block:: java

  @ProvidesIntoSet
  SerializationFeatureFlag getSerializationFeature() {
    return SerializationFeatureFlag.create(SerializationFeature.FEATURE1, true);
  }

  @ProvidesIntoSet
  SerializationFeatureFlag getSerializationFeature() {
    return SerializationFeatureFlag.create(SerializationFeature.FEATURE2, true);
  }

  @ProvidesIntoSet
  DeserializationFeatureFlag getDeserializationFeature() {
    return DeserializationFeatureFlag.create(DeserializationFeature.FEATURE_NAME, true);
  }

  @ProvidesIntoSet
  JsonGeneratorFeatureFlag getJsonGeneratorFeature() {
    return JsonGeneratorFeatureFlag.create(JsonGenerator.Feature.FEATURE_NAME, true);
  }

  @ProvidesIntoSet
  JsonParserFeatureFlag getJsonParserFeature() {
    return JsonParserFeatureFlag.create(JsonParser.Feature.FEATURE_NAME, true);
  }

  @ProvidesIntoSet
  MapperFeatureFlag getMapperFeature() {
    return MapperFeatureFlag.create(MapperFeature.FEATURE_NAME, true);
  }

FieldFilter
-----------

The ``FieldFilter`` class provides a way to filter JSON to only a set of specified fields. See the
javadoc for `FieldFilter <https://github.com/cerner/beadledom/tree/main/jackson/src/main/java/com/cerner/beadledom/jackson/filter/FieldFilter.java>`_ for
usage.
