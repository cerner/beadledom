.. _beadledom-client:

beadledom-client
================

Beadledom Client is a JAX-RS 2.0 based HTTP Client for Java. It provides a wrapper around the JAX-RS 2.0 client API along with some additional functionality including a `CorrelationIdFilter <https://github.com/cerner/beadledom/blob/master/client/beadledom-client/src/main/java/com/cerner/beadledom/client/CorrelationIdFilter.java>`_ that will set a correlation id header on all client calls.

Beadledom Client is designed to provide a common way of writing Java HTTP clients. It internally wraps another implementation of HTTP Client to execute the HTTP requests. Similarly to, and to stay consistent with, `Beadledom <https://github.com/cerner/beadledom>`_ the JAX-RS 2.0 client API provides a default implementation by wrapping the Resteasy Client for creating HTTP clients.

Understanding the `Resteasy client API <https://docs.jboss.org/resteasy/docs/3.0.12.Final/userguide/html/RESTEasy_Client_Framework.html>`_ is important for understanding the Beadledom Client API. Beadledom Client builds upon the `Resteasy <https://docs.jboss.org/resteasy/docs/3.0.12.Final/userguide/html/RESTEasy_Client_Framework.html>`_ / `JAX-RS <https://jax-rs-spec.java.net/nonav/2.0-rev-a/apidocs/javax/ws/rs/client/package-summary.html>`_ APIs, providing some opinionated defaults, but otherwise being configurable in the same way as the Resteasy API. The Beadledom Resteasy Client configures a default ``HttpEngine`` using Apache ``HttpClient``, with connection pool, for handling the requests made through Resteasy. The underlying HTTP mechanism can be overridden by setting a custom ``HttpEngine``.

However, it is totally possible to plug in another HTTP Client implementation (ex: JerseyClient) for Beadledom Client to wrap. See how to add a different `client internal implementation <subdocs/additional_implementation>`_

Download
--------

Download using Maven:

.. code-block:: xml

  <dependency>
    <groupId>com.cerner.beadledom</groupId>
    <artifactId>beadledom-client</artifactId>
    <version>[insert latest version]</version>
  </dependency>

Usage
-----

Beadledom Client can be used with JAX-RS resource annotated interfaces for creating easy to use clients.

.. code-block:: java

  public interface GithubResource {
    @GET
    @Path("/orgs/{org}/repos")
    @Produces("application/json")
    List<Repo> getRepos(@PathParam("org") String org);
  }

These resources should be wrapped in a containing class, so the injected interfaces won't get picked up by jax-rs/Beadledom if being used within another service.
The Beadledom Client Proxy can generate an implementation of the GithubResource interface.

.. code-block:: java
  public class GithubClient {
    private final GithubResource githubResource;

    GithubClient(BeadledomClient client) {
      BeadledomWebTarget target = client.target("https://api.github.com");

      githubResource = target.proxy(GithubResource.class);
    }

    public GithubResource githubResource() {
      return githubResource;
    }
  }

The client can then be used to make a call to the service.

.. code-block:: java

  BeadledomClient client = BeadledomResteasyClientBuilder.newBuilder().build();
  GithubClient githubClient = GithubClient(client);
  List<Repo> repos = githubClient.githubResource.getRepos("cerner");

All of the same JAX-RS annotations that are used for service resources can be used for the client interface. The interface can even be re-used for both the client and service, with the service implementing the interface, but keeping the JAX-RS annotations on the interface. The annotations that can be used include, but are not limited to the following. Read the JAX-RS documentation for a full set of features.

* ``@Consumes`` and ``@Produces`` for specifying media types
* ``@Path`` for specifying the resource path
* ``@GET``, ``@PUT``, ``@POST``, ``@DELETE``, etc. for HTTP Method
* ``@PathParam`` for path parameters
* ``@QueryParam`` for query parameters
* ``@HeaderParam`` for header parameters

More information about using the Beadledom Client in conjunction with Guice can be found here:
* `Usage with Guice <subdocs/guice>`_

Generic Response
----------------

The Resteasy implementation of the auto-generated client proxy provides support for ``GenericResponse``. This class provides a more typesafe mechanism for consuming a JAX-RS response.

.. code-block:: java

  public interface RepoResource {
      @GET
      @Path("/repos/{user}/{repo}")
      @Produces("application/json")
      GenericResponse<Repo> getRepos(@PathParam("user") String user, @PathParam("repo") String repo));
    }

.. code-block:: java

  GenericResponse<Repo> response = repoResource.getRepos("cerner", "beadledom");
  if (response.isSuccessful() {
    Repo repo = response.getBody();
    // Do something with the repo
  }

Using Parameters
----------------

Method parameters can be annotated with different parameter types and they will be sent as part of the request as the specified parameter type.

Headers
~~~~~~~

Header parameters will be added as request headers.

.. code-block:: java

  @GET
  @Path("/orgs/{org}/repos")
  @Produces("application/json")
  List<Repo> getRepos(@PathParam("org") String org, @HeaderParam("Time-Zone") String timezone);

Query Parameters
~~~~~~~~~~~~~~~~

Query parameters will be appended to the request URI.

.. code-block:: java

  @GET
  @Path("/orgs/{org}/repos")
  @Produces("application/json")
  List<Repo> getRepos(@PathParam("org") String org, @QueryParam("page") long page);

Path Parameters
~~~~~~~~~~~~~~~

The path parameter will fill in the matching placeholder in the ``@Path`` annotation on the method.

.. code-block:: java

  @GET
  @Path("/orgs/{org}/repos")
  @Produces("application/json")
  List<Repo> getRepos(@PathParam("org") String org);

Request Body
~~~~~~~~~~~~

A Request body for POST/PUT requests is specified as an unannotated method parameter.

.. code-block:: java

  @POST
  @Path("/orgs")
  @Produces("application/json")
  @Consumes("application/json")
  Organization createOrg(Organization org);

JAX-RS Features and Providers
-----------------------------

Existing and custom JAX-RS `Features <https://jax-rs-spec.java.net/nonav/2.0/apidocs/javax/ws/rs/core/Feature.html>`_, `Providers <https://jax-rs-spec.java.net/nonav/2.0/apidocs/javax/ws/rs/ext/Provider.html>`_, including filters (`ClientRequestFilter <https://jax-rs-spec.java.net/nonav/2.0/apidocs/javax/ws/rs/client/ClientRequestFilter.html>`_ or `ClientResponseFilter <https://jax-rs-spec.java.net/nonav/2.0/apidocs/javax/ws/rs/client/ClientResponseFilter.html>`_) wrapped with a ``Feature`` can be registered and used with the client.

.. code-block:: java

  BeadledomClient client = BeadledomResteasyClientBuilder.newBuilder()
      .register(new MyCustomProvider())
      .build();

Authentication filters or serializers/deserializers like Jackson JSON can be registered and customized to meet the requirements of calling any service.
