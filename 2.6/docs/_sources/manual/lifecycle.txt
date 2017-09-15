beadledom-lifecycle
===================

This project provides mechanisms for using Guice to manage lifecycle phases of a running application. Startup and shutdown lifecycle phases are supported through methods annotated with the JSR-250 annotations ``@PostConstruct`` and ``@PreDestroy``. Methods annotated with ``@PostConstruct`` will execute at object provision time or application startup time for eager singletons. Methods annotated with ``@PreDestroy`` will be executed when the container ``LifecycleInjectr#shutdown`` method is executed.

Download
--------

Download using Maven:

.. code-block:: xml

  <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-lifecycle</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Usage
-----

Creating a lifecycle container
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Creating a lifecycle container is simple. All that is needed is an entry point and an exit point. This could be a ``ContextListener``, a main method or anything else that fits the lifecycle pattern. The lifecycle of a container can be managed using a ``LifecycleInjector`` and the helper methods on ``GuiceLifecycleContainers``. A container class can implement the ``LifecycleContainer`` marker interface so that the ``GuiceLifecycleContainers`` helper methods can be used.

In the entry point to a container the ``GuiceLifecycleContainers#initialize(Container, List<Module>)`` method should be called to initialize the lifecycle. This method will create a new ``LifecycleInjector`` with the supplied modules list, initialize lifecycle management, and inject any injectable fields and methods available on the container itself. The returned ``LifecycleInjector`` should be stored by the container and then ``LifecycleInjector#shutdown`` should be called when the container has finished executing.

LifecycleInjector implementations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This project includes a default implementation of ``LifecycleInjector`` that supports Java 6, but it is a simple implementation and should not be the preferred implementation if Java 8 can be used in your application. If Java 8 can be used, then the Governator implementation should be used. By including the ``beadledom-lifecycle-governator`` jar on your classpath, the Governator will automatically be used instead of the default implementation.
