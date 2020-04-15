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
  CorrelationContext correlationContext = new CorrelationContextImpl();
  correlationContext.setId("correlation id");

Getting the Correlation ID
~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java
  CorrelationContext correlationContext = new CorrelationContextImpl();
  String correlationId = correlationContext.getId();

Resetting the Correlation ID
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java
  CorrelationContext correlationContext = new CorrelationContextImpl();
  correlationContext.clearId();

Use with Callable
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java
  CorrelationContext correlationContext = new CorrelationContextImpl();
  correlationContext.withId("correlation id", () -> {
          // correlationContext.getId(); will return "correlation id"
          return null;
        });
  // correlationContext.getId(); return null
