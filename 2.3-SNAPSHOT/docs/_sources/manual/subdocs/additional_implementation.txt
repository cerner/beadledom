.. _additional_implementation:

How to add an additional implementation for Beadledom Client
===============================================================

Adding implementation
---------------------

* Create a Maven Module and add the following dependencies

.. code-block:: xml

    <project>
        ...
        <dependencies>
            <dependency>
                <groupId>com.cerner.beadledom</groupId>
                <artifactId>client-common</artifactId>
            </dependency>
            <dependency>
                <groupId>com.cerner.beadledom</groupId>
                <artifactId>beadledom-client</artifactId>
            </dependency>
            <dependency>
                <groupId>javax.annotation</groupId>
                <artifactId>javax.annotation-api</artifactId>
            </dependency>
            <dependency>
                <groupId>javax.inject</groupId>
                <artifactId>javax.inject</artifactId>
            </dependency>
            <dependency>
                <groupId>javax.ws.rs</groupId>
                <artifactId>javax.ws.rs-api</artifactId>
            </dependency>
        </dependencies>
    </project>

* Add the following the classes into the new module

  * Implement the ``BeadledomClient`` interface that wraps the internal HTTP Client implementation. (ex: ``BeadledomResteasyClient``).

  * Extend the ``BeadledomWebTarget`` class and implement the abstract methods. This can be the wrapper for the corresponding ``WebTarget`` of the Client implementation. (ex: ``BeadledomResteasyWebTarget``)

  * Extend the ``BeadledomClientBuilder`` class and implement the abstract methods. This can be the wrapper for the corresponding ``Clientbuilder`` of the Client implementation. (ex: ``BeadledomResteasyClientBuilder``)

  * Implement the ``BeadledomClientBuilderFactory`` interface that creates an instance of the implementation of ``BeadledomClientBuilder``.

* Add the SPI lookup file

  * Create a file under ``src/main/resources/META-INF/services/com.cerner.beadledom.client.BeadledomClientBuilder``

  * Add the fully qualified class name of the implementation of BeadledomClientBuilder class. For example ``com.cerner.beadledom.client.resteasy.BeadledomResteasyClientBuilder``

Guice Integration
-----------------

* Add the Guice dependencies into the new module

.. code-block:: xml

    <project>
        ...
        <dependencies>
            ...

            <dependency>
                <groupId>com.google.inject</groupId>
                <artifactId>guice</artifactId>
            </dependency>
            <dependency>
                <groupId>com.google.inject.extensions</groupId>
                <artifactId>guice-multibindings</artifactId>
            </dependency>
        </dependencies>
    </project>

* Add a binding for the ``BeadledomClientBuilderFactory``, specific to the binding annotation.

.. code-block:: java

    OptionalBinder.newOptionalBinder(binder(), Key.get(BeadledomClientBuilderFactory.class, clientBindingAnnotation)))
                  .setBinding().toInstance(new MyNewClientbuilderFactory()); 
 
