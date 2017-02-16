beadledom-jaxrs-genericresponse
===============================

Overview
--------

This project provides an alternative response class for JAX-RS. The ``GenericResponse`` can be used
in place of the JAX-RS ``Response`` class. The ``GenericResponse`` class provides additional compile
time type safety around the retrieval of the response entity body. 

Download
--------

Download using Maven:

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-jaxrs-genericresponse</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Usage
-----

From the server side a ``GenericResponse`` can be created through the ``GenericResponses`` static methods.

.. code-block:: java

  return GenericResponses.ok(entityBody);

From the client side the entity body can be retrieve from a ``GenericResponse`` through the ``body`` method.

.. code-block:: java

  MyObject entity = genericResponse.body();
