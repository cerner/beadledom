beadledom-pagination
==================

Provides pagination query parameters and hypermedia links for paginated endpoints.

The following components are included:

`BeadledomPaginationModule <https://github.com/cerner/beadledom/blob/master/pagination/src/main/java/com/cerner/beadledom/pagination/BeadledomPaginationModule.java>`_
  a Guice module that provides configurable pagination parameters and binds the OffsetPaginatedListLinksWriterInterceptor

Download
--------

 Download using Maven:

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-pagination</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

Getting started
~~~~~~~~~~~~~~~

To add pagination to your service install the BeadledomPaginationModule in your own Guice module.
Then update your paginated resource to accept `OffsetPaginationParameters <https://github.com/cerner/beadledom/blob/master/pagination/src/main/java/com/cerner/beadledom/pagination/parameters/OffsetPaginationParameters.java>`_ and return an `OffsetPaginatedList <https://github.com/cerner/beadledom/blob/master/pagination/src/main/java/com/cerner/beadledom/pagination/src/main/java/com/cerner/beadledom/pagination/OffsetPaginatedList.java>`_.

Guice Module:

.. code-block:: java

  public class ExampleModule extends AbstractModule {
    @Override
    protected void configure() {
      install(new ResteasyModule());
      install(new BeadledomPaginationModule());
      
      bind(ExampleResource.class);
    }
  }

Example resource:

.. code-block:: java

  @Path("/example")
  public interface ExampleResource {
    @GET
    @Produces("application/json")
    OffsetPaginatedList<ExampleDto> index(@BeanParam OffsetPaginationParameters paginationParams);
  }

Configurable query parameters
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

By default the query parameters for pagination are ``offset`` and ``limit``. When not provided by the caller ``offset`` defaults to ``0`` and ``limit`` defaults to ``20``. Both the name and default values are configurable. See `BeadledomPaginationModule <https://github.com/cerner/beadledom/blob/master/pagination/src/main/java/com/cerner/beadledom/pagination/BeadledomPaginationModule.java>`_ for details.
