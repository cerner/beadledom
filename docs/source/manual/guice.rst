beadledom-guice
===============

Overview
--------

This project is an extension to `Guice core <https://github.com/google/guice/tree/main/core>`_ - contains Guice specific utilities.

Download
--------

 Download using Maven:

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-guice</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

* **BindingAnnotations** - Validates the Guice binding annotations.
    * *isBindingAnnotation* - returns **true** if the argument is a binding annotation i.e., the given annotation should be annotated with either ``@BindingAnnotation`` or ``@Qualifier``, otherwise **false**.
      * Example: :java:`BindingAnnotations.isBindingAnnotation(classOf[Override]) // false`

    * *checkIsBindingAnnotation* - throws the ``IllegalArgumentException`` exception if the annotation is not the ``BindingAnnotation`` or a ``Qualifier``, otherwise returns the argument.
      * Example: :java:`BindingAnnotations.checkIsBindingAnnotation(classOf[TestBindingAnnotation]) // TestBindingAnnotation`

