beadledom-swagger2
==================

This project provides API documentation via `Swagger 2 <https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md>`_.

Download
--------

 Download using Maven:

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-swagger2</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

Install the `Swagger2Module <https://github.com/cerner/beadledom/blob/master/swagger2/src/main/java/com/cerner/beadledom/swagger2/Swagger2Module.java>`_ to enable
the ``/api-docs`` endpoint as well as a ``/meta/swagger/ui`` endpoint containing the Swagger ui.

.. code-block:: java

  public class MyModule extends AbstractModule {
    ...

    @Override
    protected void configure() {
      ...
      install(new Swagger2Module());
      ...
    }

    @Provides
    Info provideSwagger2Info(ServiceMetadata serviceMetadata) {
      return new Info()
        .title("Your Service Title")
        .description("Your Service Description")
        .version(serviceMetadata.getGuildInfo().getVersion());
    }
  }
