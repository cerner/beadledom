.. _simple-service:

simple-service
==============

Defines a maven archetype for a basic Beadledom service.

Usage
-----

``simple-service`` is the default archetype used by the `beadledom bootstrap utility <../bootstrap/README.rst>`_. So
you may either run:

.. code-block:: bash

   beadledom new

or if you rather be more explicit:

.. code-block:: bash

   beadledom new simple-service

The result of this command creates a basic Beadledom RESTful service with the following sub-modules:

- api     - Interfaces and models defining the request and responses of the service
- client  - Consumes ``api`` module to generate a Java client for consumers of the service
- service - Implementation of ``api`` module to define the logic of the service

--------

After maven finishes creating the project from the ``simple-service`` archetype you will be able
to switch to the new project directory and run:

.. code-block:: bash

   mvn clean install

Once the build has finished navigate to the ``service`` module and use maven to start up your new service:

.. code-block:: bash

   > cd service
   service> mvn tomcat7:run-war

After the server starts you can open your favorite web browser and navigate to several uris already provided
to you either by the archetype or Beadledom itself. The easiest way to see what all your service can do
is to explore the Swagger documentation.

.. code-block:: base

   http://localhost.com:8080/{service-name}/meta/swagger/ui

Here you should be able to see all of the ``healthcheck`` resources as well as the ``hello`` resource.
