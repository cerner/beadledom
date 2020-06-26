.. _client_guice:

Using Beadledom Client with Guice
===================================

The ``beadledom-client-guice`` module adds the ability to use Guice for building and customizing clients. JAX-RS features, providers, and filters can all be registered by binding them with Guice. To avoid conflicts and duplicate binding errors client Guice configuration must be namespaced using a binding annotation; both ``@BindingAnnotation`` and ``@Qualifier`` annotations being supported.

Download
--------

Download using Maven:

.. code-block:: xml

  <dependency>
    <groupId>com.cerner.beadledom</groupId>
    <artifactId>beadledom-client-guice</artifactId>
    <version>[insert latest version]</version>
  </dependency>

Usage
-----

Installing the ``BeadledomClientModule`` will make a ``BeadledomClient`` object available that can be used for proxying JAX-RS resource interfaces and then making those resources available through Guice. It is recommended to create a wrapper class such as ``MyClient`` that has accessor methods to the individual JAX-RS resources rather than directly making the JAX-RS resources available through Guice. The reason for this, is that the clients may be used within a Beadledom service, and the JAX-RS client resources that are directly available through Guice will get picked up by the server and it will attempt to use them to serve traffic at the client paths.

.. code-block:: java
  public class MyClient {
    private final MyResource myResource;

    MyClient(BeadledomClient client, MyClientConfig config) {
      BeadledomWebTarget target = client.target(config.uri());

      this.myResource = target.proxy(MyResource.class);
    }

    public MyResource myResource() {
      return myResource;
    }
  }

.. code-block:: java

  public class MyClientModule extends AbstractModule {
    @Override
    protected void configure() {
      // Bind the client module with this client's binding annotation
      // so that the client is namespaced for this client only.
      install(BeadledomClientModule.with(MyClientAnnotation.class));

      // Consumers of the client should provide the configuration.
      requireBinding(MyClientConfig.class);
    }

    @Provides
    @Singleton
    MyClient provideMyClient(
        @MyClientAnnotation BeadledomClient client, MyClientConfig config) {
      return new MyClient(client, config);
    }
  }

Features
--------

JAX-RS features, providers can be bound with the same binding annotation as a particular ``BeadledomClient`` and they will automatically be registered with the client. The ``beadledom-client-guice`` project includes a Jackson provider configured to match the same default JSON configuration from beadledom based services.

Jackson JSON Provider
~~~~~~~~~~~~~~~~~~~~~

The Beadledom JSON Provider client feature includes the ``ObjectMapperClientFeatureModule`` for installing the Jackson JSON Provider. The feature can be included by installing the module with the same binding annotation as the ``BeadledomClientModule``.

.. code-block:: java

  public class MyClientModule extends AbstractModule {
    @Override
    protected void configure() {
      install(BeadledomClientModule.with(MyClientAnnotation.class));
      install(ObjectMapperClientFeatureModule.with(MyClientAnnotation.class));

      // Additional configuration
    }
  }

Follow the below steps to configure the client specific ObjectMapper

* All the ``@ProvidesIntoSet`` methods must also be annotated with the client's BindingAnnotation i.e., ``@MyClientAnnotation`` from the above example.

.. code-block:: java

  @ProvidesIntoSet
  @MyClientAnnotation
  SerializationFeatureFlag getSerializationFeature() {
    return SerializationFeatureFlag.create(SerializationFeature.INDENT_OUTPUT, true);
  }

* When defining the multibinders make sure to pass in the client's BindingAnnotation as well.

.. code-block:: java

  Multibinder.newSetBinder(binder(), Module.class, MyClientAnnotation.class);

For enabling/disabling Jackson's ObjectMapper features, please refer to the beadledom's jackson `documentation <https://github.com/cerner/beadledom/tree/main/jackson#jackson-objectmapper-features>`_.

Client Configuration
~~~~~~~~~~~~~~~~~~~~

``BeadledomClientConfiguration`` can be used to add custom configurations to the client. It is important that ``BeadledomClientConfiguration`` gets bound to a client annotation.

The client options that can be configured are:

connectionPoolSize
  Sets the connection pool size.

connectionTimeoutMillis
  Sets the connection timeout to be used in milliseconds.

correlationIdName
  Sets the Header name for a client.

maxPooledPerRouteSize
  Sets the max connection pool size per route.

socketTimeoutMillis
  Sets the socket timeout to be used in milliseconds.

sslContext
  Sets the SSL Context.

trustStore
  Sets the SSL trust store.

ttlMillis
  Sets the TTL to be used in milliseconds.

verifier
  Sets the hostname verifier.

.. code-block:: java

  @Provides
  @MyAmazingFeature
  BeadledomClientConfiguration provideClientConfig () {
    BeadledomClientConfiguration beadledomClientConfig = BeadledomClientConfiguration.builder()
        .maxPooledPerRouteSize(60)
        .socketTimeoutMillis(60)
        .connectionTimeoutMillis(60)
        .ttlMillis(60)
        .connectionPoolSize(60).build();
    return beadledomClientConfig;
  }

Custom Features
~~~~~~~~~~~~~~~

Additional JAX-RS features and providers can be installed by following a similar pattern to the Jackson JSON module.

Start by creating a client feature Guice module. It's usually ideal to extend from the Guice ``AbstractModule`` and only expose the bindings that should be available for the client. This is useful when there are additional bindings that are required for the feature/provider, but that shouldn't be bound/registered with the client or made available to consumers of the client.

The constructor for the client feature module should be private and paired with a static factory method ``with(Class<? extends Annotation> annotation)`` since the feature must be namespaced to the same client binding annotation that the feature is being installed for. This is required to prevent duplicate binding issues when multiple clients are in use.

.. code-block:: java

  public class MyClientFeatureModule extends AbstractModule {
    private final Class<? extends Annotation> annotation;

    private MyClientFeatureModule(Class<? extends Annotation> annotation) {
      this.annotation = annotation;
    }

    public static MyClientFeatureModule with(Class<? extends Annotation> annotation) {
      // The annotation must be either BindingAnnotation or Qualifier; fail fast otherwise
      BindingAnnotations.checkIsBindingAnnotation(annotation);

      return new MyClientFeature(annotation));
    }

    @Override
    protected void configure() {
      bind(MyClientFeature.class).annotatedWith(annotation).toProvider(MyClientFeatureProvider.class);

      expose(MyClientFeature.class).annotatedWith(annotation);
    }
  }

When additional configuration is needed for creating a client feature, then the ``DynamicBindingProvider`` class can be used for creating a custom Guice provider.

.. code-block:: java

  class MyConfigurableClientFeature {
      public static MyConfigurableClientFeature create(MyConfiguration config) {
        return new MyConfigurableClientFeature(config);
      }

      // ...
  }

  class MyConfigurableClientFeatureProvider implements Provider<MyConfigurableClientFeature> {
    private final Class<? extends Annotation> annotation;

    private DynamicBindingProvider<MyConfiguration> configurationProvider;

    MyConfigurableClientFeatureProvider(Class<? extends Annotation> annotation) {
      this.annotation = annotation;
    }

    @Inject
    void init(DynamicBindingProvider<MyConfiguration> configurationProvider) {
      this.configurationProvider = configurationProvider;
    }

    @Override
    public OAuth1ClientFilterFeature get() {
      // Get the configuration that is namespaced with the same binding annotation
      MyConfiguration config = configurationProvider.get(annotation);

      return MyConfigurableClientFeature.create(config);
    }
  }

.. code-block:: java

  public class MyConfigurableClientFeatureModule extends PrivateModule {
    private final Class<? extends Annotation> annotation;

    private MyConfigurableClientFeatureModule(Class<? extends Annotation> annotation) {
      this.annotation = annotation;
    }

    public static MyConfigurableClientFeatureModule with(Class<? extends Annotation> annotation) {
      // The annotation must be either BindingAnnotation or Qualifier; fail fast otherwise
      BindingAnnotations.checkIsBindingAnnotation(annotation);

      return new MyClientFeature(annotation));
    }

    @Override
    protected void configure() {
      // Use the DynamicAnnotations helper to bind a dynamic provider for the configuration class
      // This will make the DynamicProvider<MyConfiguration> available for injection and also
      // require a binding for MyConfiguration that must be provided either by a consumer's
      // module or one of the client modules.
      DynamicAnnotations
          .bindDynamicProvider(binder(), MyConfiguration.class, annotation);
      MyConfigurableClientFeatureProvider provider =
          new MyConfigurableClientFeatureProvider(annotation);
      bind(MyConfigurableClientFeature.class).annotatedWith(annotation)
          .toProvider(provider);

      // By using PrivateModule and only exposing the feature this is meant to install, the
      // dynamic provider from above is hidden from consumers since it is an unnecessary
      // implementation detail.
      expose(MyConfigurableClientFeature.class).annotatedWith(annotation);
    }
  }
