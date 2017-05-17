.. _beadledom-jaxrs:

beadledom-jaxrs
===============

This module provides a guice module for providing JAX-RS specific bindings.

Correlation Id
--------------

The purpose of the correlation id is to associate request/response interactions. This allows tracing
of an originating request throughout a system. This is helpful for debugging and logging purposes.

The CorrelationIdFilter reads the correlation id header from the request, adds it to the
response headers, and adds it to the slf4j Mapped Diagnostic Context (MDC).

The correlation id will be taken from the correlation id request header if present, or
generated if absent. The id will also be added to the response and to the MDC for logging.

The default header and mdc key name is ``Correlation-Id``. However, both names are
configurable through guice bindings. |usageLink|_.

To include the correlation id in the catalina.out file using log4j, update the log format to include
``%X{Correlation-Id}`` replacing the name with your configured name.

.. |usageLink| replace:: More on this later
.. _usageLink: `Usage`_

PATCH
--------------

`PATCH is a HTTP verb (similar to GET, POST, DELETE, PUT) to push partial changes to the REST resource. JAX-RS by default doesn't support PATCH hence Beadledom adds the support for PATCH explicitly.

For more details on the PATCH method visit the PATCH RFC_.

To use the PATCH method and annotation for your resource, simply annotate your JAX-RS resource method with the PATCH annotation as shown below

.. code-block:: java

 @PATCH
 @Path("path/to/patch")
 @Produces(MediaType.APPLICATION_JSON)
 public Response patch(
   @PathParam("id") final Long id,
   @ApiParam(value = "changes to make to the object with the specified id")
   PatchObject patchObject);

.. _RFC: https://tools.ietf.org/html/rfc5789
Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-jaxrs</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Usage
-----
 
The correlation id header and/or MDC name can overridden by adding a binding for your custom name
and annotating it with ``@CorrelationIdHeader`` or ``@CorrelationIdMdc`` respectively.

.. code-block:: java

  public class MyModule extends AbstractModule {

    @Override
    public void configure() {
      install(new JaxRsModule());

      String customCorrelationIdName = "Not-Default";
      bind(String.class).annotatedWith(CorrelationIdHeader.class).toInstance(customCorrelationIdName);
      bind(String.class).annotatedWith(CorrelationIdMdc.class).toInstance(customCorrelationIdName);
    }
  }

or

.. code-block:: java

  public class MyModule extends AbstractModule {

    private final String customCorrelationIdName = "Not-Default";

    @Override
    public void configure() {
      install(new JaxRsModule());
    }

    @Provides
    @CorrelationIdHeader
    public String provideCorrelationIdHeader() {
      return customCorrelationIdName;
    }

    @Provides
    @CorrelationIdMdc
    public String provideCorrelationIdMdc() {
      return customCorrelationIdName;
    }
  }
