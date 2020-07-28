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

`JAX-RS 2.0 (see section 3.3 for Resource Methods) <http://download.oracle.com/otn-pub/jcp/jaxrs-2_0_rev_A-mrel-eval-spec/jsr339-jaxrs-2.0-final-spec.pdf?AuthParam=1494975982_179302191fa8833291d2b6647856d11b>`_ does not require implementations to support the
`PATCH <https://en.wikipedia.org/wiki/Patch_verb>`_ HTTP method. This is likely due to the fact that ``PATCH`` was introduced in a later `rfc <https://tools.ietf.org/html/rfc5789>`_ that added
the new HTTP method to the already existing HTTP/1.1 specification.

``@PATCH`` was added to `beadledom-jaxrs <https://github.com/cerner/beadledom/tree/main/jaxrs>`_ to allow services to support partial updates without the need of
overloading ``@POST``. The annotation has no opinion on how the service decides to implement the
resource performing the ``PATCH`` operation. Implementing services have the freedom to support `JSON
Patch <https://tools.ietf.org/html/rfc6902>`_ and/or `JSON Merge Patch <https://tools.ietf.org/html/rfc7386>`_.

As long as a service has ``beadledom-jaxrs`` as a dependency ``@PATCH`` can be used just like any of the
HTTP method annotations defined by JAX-RS. Below is a small example of ``@PATCH`` being used in an
interface for a resource.

.. code-block:: java

 @PATCH
 @Path("path/to/patch")
 @Produces(MediaType.APPLICATION_JSON)
 public Response patch(
   @PathParam("id") final Long id,
   @ApiParam(value = "changes to make to the object with the specified id")
   PatchObject patchObject);

.. _RFC: https://tools.ietf.org/html/rfc5789

Forwarded Header Filter
-----------------------

The purpose of the ``ForwardedHeaderFilter`` is to determine if the client request was made from a secure protocol (HTTPS). If the request object picked up by Beadledom is http, but either the "Forwarded" or "X-Forwarded-Proto" headers are set to indicate that the request originated from a secure context (https), the internal request is changed to use https.
This is primarily done so that when we build URL's to send back to the consuming service or application, they will have the same http scheme as the original request.

This Header Filter is available to install for any consuming application.

.. code-block:: java
  public class MyModule extends AbstractModule {

    @Override
    public void configure() {
      install(new JaxRsModule());

      bind(ForwardedHeaderFilter.class).in(Singleton.class);
    }
  }

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
