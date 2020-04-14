.. _beadledom-correlation:

beadledom-correlation
======================

This project encapsulates and provides access to the Correlation ID of the current transaction. A Correlation ID is a unique identifier that can be used to connect (correlate) calls through multiple services that are part of a single transaction. This project provides access to the current Correlation Id and allows frameworks to control the lifecycle of the Correlation Id.
 
Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-correlation</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

Setting the Correlation ID
~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java
  CorrelationIdContext correlationIdContext = new CorrelationIdContextImpl();
  correlationIdContext.set("correlation id");

Getting the Correlation ID
~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java
  CorrelationIdContext correlationIdContext = new CorrelationIdContextImpl();
  String correlationId = correlationIdContext.get();

Resetting the Correlation ID
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java
  CorrelationIdContext correlationIdContext = new CorrelationIdContextImpl();
  correlationIdContext.reset();

Use Within try-with-resources Block
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java
  CorrelationIdContext correlationIdContext = new CorrelationIdContextImpl();
  try (CorrelationIdCloseable correlationIdCloseable = correlationIdContext.setCloseable("correlation id")) {
      // correlationIdContext.get(); will return "correlation id"
      // correlationIdContext.reset(); is called when implicit close() is called in correlationIdCloseable
  }
  // correlationIdContext.get(); return null
