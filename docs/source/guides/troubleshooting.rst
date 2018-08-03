.. toctree::
   :name: troubleshooting
   :includehidden:

Troubleshooting
===============

Healthchecks
------------

I am seeing ``Optional[xxx]`` inside of my healthchecks
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Mustache dependency resolution issue
""""""""""""""""""""""""""""""""""""

Problem
  The ``beadledom-health`` module has a dependency on ``com.github.spullara.mustache.java:compiler:0.9.0``. Some
  older versions of ``com.github.spullara.mustache.java:compiler`` do not have support for Java 8 which beadledom 
  health needs. Not having Java 8 support would result in ``Optional`` fields not being unwrapped properly.
  

Suggested solution
  Add an explicit dependency on version ``0.9.0`` of the ``com.github.spullara.mustache.java:compiler`` dependency

Build Exceptions
----------------

I am seeing ``scala.collection.immutable.$colon$colon.hd$1()Ljava/lang/Object;`` a part of my exceptions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Scala version collision issue
"""""""""""""""""""""""""""""

Problem
  What this usually telling you is that you have a scala version mismatch (Scala classes from 2.10 and 2.11 or 2.11 and 2.12 etc.).
  The best way we have found to resolve these inconsistencies is to do `mvn dependency:tree -Dincludes:scala-library::` and search
  the output for anything containing the unwanted version of Scala. For instance `scalatest_2.10` if you were using ``Scala 2.11``.


Swagger
-------

I am seeing resources from clients being treated as service resource by swagger
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Problem
  Beadledom clients are automatically generated from API resources annotated with JAX-RS and Swagger annotations.
  API definitions from clients used in a service is getting merged with the service definition.

Suggested solution
  Make your service module a private module, and expose only your server resources. In another module install ``ResteasyModule`` and bootstrap all the configurations needed for resteasy.
  In your ``ResteasyContextListener`` class install your service module and ``ResteasyBootstrapModule``.

