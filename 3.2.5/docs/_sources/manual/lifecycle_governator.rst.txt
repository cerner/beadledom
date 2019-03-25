beadledom-lifecycle-governator
==============================

This module implements lifecycle management using netflix's `governator <https://github.com/Netflix/governator>`_ library.
 
.. note::
  * This is an internal module that is strictly confined to the internal consumption ONLY and should not be used directly, please use `lifecycle <lifecycle>`_ module.
  * If the consuming project is Java8+ and if this module is included on the classpath then this will be automatically used by the lifecycle module.

Download
--------

Download using Maven:

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-lifecycle-governator</artifactId>
      <version>[insert latest version]</version>
  </dependency>
