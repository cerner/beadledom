beadledom-testing
=================

This project adds some additional tooling to make the JAX-RS services testing easier.

Download
--------

Download using Maven:

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
         <artifactId>beadledom-testing</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

JsonMatchers
  check out the `scala doc <https://github.com/cerner/beadledom/blob/master/testing/src/main/scala/com/cerner/beadledom/testing/JsonMatchers.scala>`_ on the trait for the usage instructions.

UnitSpec
  Base spec for including ``FunSpec`` and Scalatest Mixins
