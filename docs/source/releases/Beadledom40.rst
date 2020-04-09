.. _4.0:

Beadledom 4.0
=============

To be released...

New Features
------------

- TBD

Migrating from Beadledom 3.0
----------------------------

Maven Module Changes
~~~~~~~~~~~~~~~~~~~~
The beadledom-swagger1 module was removed. This will require updating your poms that have a dependency
on beadledom-swagger1 to beadledom-swagger2. The user guide for beadledom-swagger2 can be found `here <https://engineering.cerner.com/beadledom/3.4/docs/manual/swagger2.html/>`_.

.. code-block:: xml

  <dependency>
    <groupId>com.cerner.beadledom</groupId>
    <artifactId>beadledom-swagger2</artifactId>
    <version>4.0</version>
  </dependency>

The beadledom-stagemonitor module was removed. This will require updating your poms that have a dependency
on beadledom-stagemonitor.

Upgrading to Scala 2.12 and Scalatest 3.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Beadledom upgraded to Scala 2.12 and Scalatest 3.1. If your project depends on any of the Scala functionality provided by Beadledom (naming beadledom-testing), then you will also need to upgrade these dependencies. It's best to make these upgrades even if you don't depend on the Scala functionality from Beadledom.

Scalatest provides autofix mechanisms for this upgrade, but requires multiples steps.

1. Upgrade to the latest Scala 2.12 version and scalatest 3.0.8 to use the scalatest autofixes for 3.0.8
  * Scala binary version: 2.12
  * Scala version: 2.12.11+
  * scala-maven-plugin version: 4.3.1+
  * scalacheck version: 1.14.3+
  * scalatest version: 3.0.8
  * Add semanticdb compiler plugin to the scala-maven-plugin configuration
    .. code-block:: xml
      <compilerPlugins>
        <compilerPlugin>
          <groupId>org.scalameta</groupId>
          <artifactId>semanticdb-scalac_${scala.version}</artifactId>
          <version>4.3.7</version>
        </compilerPlugin>
      </compilerPlugins>
  * Add scalafix-maven-plugin with the scalatest autofix version 3.0.8-0 dependency to your pom file
    .. code-block:: xml
      <plugin>
        <groupId>io.github.evis</groupId>
        <artifactId>scalafix-maven-plugin</artifactId>
        <version>0.1.2_0.9.5</version>
        <dependencies>
          <dependency>
            <groupId>org.scalatest</groupId>
            <artifactId>autofix_${scala.binary.version}</artifactId>
            <version>3.0.8-0</version>
          </dependency>
        </dependencies>
      </plugin>
  * Add a new file to the root of your project called `.scalafix.conf` with the following contents
    ..code-block::
      rules = [
        RenameDeprecatedPackage
      ]
  * Run `mvn clean install scalafix:scalafix` and fix any errors until you get a fully successful build.
2. Upgrade scalatest to 3.1.1+ with the updated autofix
  * scalatest version: 3.1.1+
  * Update scalafix-maven-plugin configuration
    .. code-block:: xml
      <plugin>
      <groupId>io.github.evis</groupId>
      <artifactId>scalafix-maven-plugin</artifactId>
      <version>0.1.2_0.9.5</version>
      <dependencies>
        <dependency>
          <groupId>org.scalatest</groupId>
          <artifactId>autofix_${scala.binary.version}</artifactId>
          <version>3.1.0.0</version>
        </dependency>
      </dependencies>
    </plugin>
  * Add the newly modularized scalatest jars to your dependency management and dependencies. The below dependencies are the most commonly used ones, but there may be others you'll need to add.
    .. code-block:: xml
      <dependency>
        <groupId>org.scalatestplus</groupId>
        <artifactId>junit-4-12_${scala.binary.version}</artifactId>
        <version>3.1.1.0</version>
        <scope>test</scope>
      </dependency>
      <dependency>
        <groupId>org.scalatestplus</groupId>
        <artifactId>mockito-3-2_${scala.binary.version}</artifactId>
        <version>3.1.1.0</version>
        <scope>test</scope>
      </dependency>
      <dependency>
        <groupId>org.scalatestplus</groupId>
        <artifactId>scalacheck-1-14_${scala.binary.version}</artifactId>
        <version>3.1.1.0</version>
        <scope>test</scope>
      </dependency>
  * Update the contents of your `.scalafix.conf` file
    .. code-block:: xml
      rules = [
        RewriteDeprecatedNames
      ]
  * Run `mvn clean install scalafix:scalafix` and fix any errors until you get a fully successful build.
  * Manually fix broken imports due to the switch to the modularized jars above and continue the above command until the entire thing succeeds.
3. Remove the scalafix and autofix configurations
  * Delete the `.scalafix.conf` file
  * Remove the scalafix-maven-plugin
  * Remove the semanticdb compiler plugin

