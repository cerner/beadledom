.. _3.0:

Beadledom 3.0
=============

Released <Date>

New Features
------------

Migrating from Beadledom 2.0
----------------------------
Beadledom 3.0 no longer installs StagemonitorModule, SwaggerModule, AvroJacksonGuiceModule,
AvroSwaggerGuiceModule, and HealthModule modules with BeadledomModule. If the removed functionality is
desired, install the removed modules in the consuming Guice module. ResteasyModule installs the
BeadledomModule module, so the removed modules also apply.

.. code-block:: java

  public class MyModule extends AbstractModule {

    protected void configure() {
      install(new ResteasyModule());
    }
  }

  // would become if all removed functionality is desired

  public class MyModule extends AbstractModule {

    protected void configure() {
      install(new ResteasyModule());
      install(new StagemonitorModule());
      install(new SwaggerModule());
      install(new AvroJacksonGuiceModule());
      install(new AvroSwaggerGuiceModule());
      install(new HealthModule());
    }
  }

JAX-RS 2.1 Upgrade
~~~~~~~~~~~~~~~~~~
Beadledom 3.0 now uses JAX-RS 2.1 and Resteasy 3.6.x. Change your dependencies on javax.ws.rs-api
to 2.1 and Resteasy to 3.6.0.Final or the latest 3.6.x version.
