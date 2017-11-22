beadledom-swagger
=================

This project provides API documentation via `Swagger <http://swagger.io/>`_.
Additionally the Swagger UI has been updated with an optional field for an Authentication Header.

Download
--------

 Download using Maven:

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-swagger</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

Install the `SwaggerModule <https://github.com/cerner/beadledom/blob/master/swagger/src/main/java/com/cerner/beadledom/swagger/SwaggerModule.java>`_ to enable
the ``/api-docs`` endpoint as well as a ``/meta/swagger/ui`` endpoint containing the Swagger ui.

.. code-block:: java

  public class MyModule extends AbstractModule {
    ...

    @Override
    protected void configure() {
      ...
      install(new SwaggerModule());
      ...
    }

    @Provides
    SwaggerConfig provideSwaggerConfig(ServiceMetadata serviceMetadata) {
      SwaggerConfig config = new SwaggerConfig();
      config.setApiVersion(serviceMetadata.getBuildInfo().getVersion());
      config.setSwaggerVersion(SwaggerSpec.version());
      return config;
    }
  }
