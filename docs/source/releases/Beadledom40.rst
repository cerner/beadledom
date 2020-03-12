.. _4.0:

Beadledom 4.0
=============

To be released...

New Features
------------

- TBD

Migrating from Beadledom 3.0
----------------------------

Maven Module Changes
~~~~~~~~~~~~~~~~~~~~
The beadledom-swagger1 module was removed. This will require updating your poms that have a dependency
on beadledom-swagger1 to beadledom-swagger2. The user guide for beadledom-swagger2 can be found `here <https://engineering.cerner.com/beadledom/3.4/docs/manual/swagger2.html/>`_.

.. code-block:: xml

  <dependency>
    <groupId>com.cerner.beadledom</groupId>
    <artifactId>beadledom-swagger2</artifactId>
    <version>4.0</version>
  </dependency>

The beadledom-stagemonitor module was removed. This will require updating your poms that have a dependency
on beadledom-stagemonitor.

The beadledom-client module has been deprecated. Use Square's `Retrofit <https://github.com/square/retrofit>`_ instead.
