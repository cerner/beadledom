.. _beadledom-guice-dynamicbindings:

beadledom-guice-dynamicbindings
===============================

Overview
--------

Enables creation of multiple bindings for the same `Java Type <https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Type.html>`_
to be used for different purposes. ``DynamicBindings`` are namespaced to ensure that all bindings
for the same ``Type`` are kept separate.

Namespacing may be achieved using either BindingAnnotation_ or Qualifier_. Additionally Guice
provides a way to namespace bindings using BindingAnnotations_. However, it is not always possible
to provide or retrieve bindings at compile time when using ``BindingAnnotations``. This module also
provides a way to retrieve bindings for a ``Type`` given a specific ``Annotation`` at runtime,
assuming the relationship between the ``Annotation`` and ``Type`` was created ahead of time.
 
Download
--------
 
Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-guice-dynamicbindings</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----
 
* Binding a dynamic provider

.. code-block:: java

  public Mymodule extends AnnotatedModule {
    ...

    public Mymodule(Class<? extends Annotation> bindingAnnotation) {
        super(bindingAnnotation);
    }

    @Override
    protected void configure() {
      ...
      bind(MyClass.class).annotatedWith(getBindingAnnotation).toInstance(new MyClass());
      bindDynamicProvider(MyClass.class);
      ...
    }
  }

* Retrieving the dynamically bound provider

.. code-block:: java

  public Injector injector = Guice.createInjector(new MyModule())
  TypeLiteral typeLiteral = new TypeLiteral<DynamicBindingProvider<MyClass>>() {}
  Provider provider = injector.getInstance(Key.get(typeLiteral))

  provider.get(MyBindingAnnotation.class) // instance of the MyClass bound to MyBindingAnnotation

.. _BindingAnnotations: https://github.com/google/guice/wiki/BindingAnnotations
.. _BindingAnnotation: https://google.github.io/guice/api-docs/4.1/javadoc/com/google/inject/BindingAnnotation.html
.. _Qualifier: http://docs.oracle.com/javaee/6/api/javax/inject/Qualifier.html
