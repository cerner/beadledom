beadledom-configuration
=======================

Overview
--------

This project exposes a `immutable
configuration <https://commons.apache.org/proper/commons-configuration/userguide_v1.10/overview.html>`__
object via a Guice module. It lets the consumer access the configuration
loaded from different sources in a consistent way via
`ImmutableHierarchicalConfiguration <https://commons.apache.org/proper/commons-configuration/apidocs/org/apache/commons/configuration2/ImmutableHierarchicalConfiguration.html>`__,
an immutable hierarchical configuration.

.. code:: java

  public class MyClass {
      @Inject
      public MyClass(ImmutableHierarchicalConfiguration config) {
        String s = config.getString("my_awesome_key");
        int i = config.getInt("my_awesome_int_key");
        String anotherString = config.getString("my.awesome.properties.stringKey");
      }
  }

Download
--------

Download using Maven:

.. code:: xml

  <dependency>
      <groupId>com.cerner.beadledom</groupId>
      <artifactId>beadledom-configuration</artifactId>
      <version>[insert latest version]</version>
  </dependency>

Supported Configurations
------------------------

Configurations can exist in many different formats like xml, java
properties etc. Configurations are loaded into the immutable
hierarchical configuration via the corresponding
`ConfigurationSource <https://github.com/cerner/beadledom/blob/master/configuration/src/main/java/com/cerner/beadledom/configuration/ConfigurationSource.java>`__\.
**Configuration** and **ConfigurationSource** share one to one
relationship with each other.

Each ConfigurationSource is associated with a Priority (a **positive**
integer) that determines the load order into the immutable hierarchical
configuration. Each ConfigurationSource is assigned a default priority,
however the consumer can override the priority at the time of object
creation. These priorities acts as the natural ordering on the
ConfigurationSources. The higher priority configuration sources takes
precedence over the lower priority configuration sources. If a
configuration key is found in two different sources, the value from the
higher priority source will be in the final configuration and the value
from the lower priority source will not be present in the final
configuration.

Below is the list of ConfigurationSources along with their default
priorities that Beadledom supports. It is important to note that the consumers
are **NOT** restricted to use only the below configuration formats. If
required, consumers can provide a custom configuration sources and load it via the
ConfigurationSourcesModuleBuilder as explained in the `usage <#usage>`__. For more information on
defining a custom configuration sources see `Adding Custom ConfigurationSource <#adding-custom-configurationSource>`__ 
and `Defining a custom natural ordering <#defining-a-custom-natural-ordering>`__ sections.

+--------------------------------------------------------------------------+--------------------+
| Configuration type                                                       | Default Priority   |
+==========================================================================+====================+
| `Environment Configuration <#environment-configuration>`__               | 400                |
+--------------------------------------------------------------------------+--------------------+
| `Jndi Configuration <#jndi-configuration>`__                             | 300                |
+--------------------------------------------------------------------------+--------------------+
| `Properties Configuration <#properties-configuration>`__                 | 200                |
+--------------------------------------------------------------------------+--------------------+
| `XML Configuration <#xml-configuration>`__                               | 100                |
+--------------------------------------------------------------------------+--------------------+

Environment Configuration
~~~~~~~~~~~~~~~~~~~~~~~~~

Environment variables are a common way to configure applications in a `Twelve-Factor App
<https://12factor.net/config>`__.

To load the environment-based configuration use

.. code:: java

  EnvironmentConfigurationSource environmentConfigurationSource = EnvironmentConfigurationSource.create());

JNDI Configuration
~~~~~~~~~~~~~~~~~~

JNDI is primarily used to look up the configuration by name from a
context. In a typical tomcat deployment this would be the
``context.xml`` file.

.. code:: xml

  <?xml version="1.0" encoding="UTF-8"?>
  <Context reloadable="true">
      <Environment name="keys/key1"
                   value="value1"
                   type="java.lang.String"
                   override="false"/>
      <Environment name="keys/key2"
                   value="value2"
                   type="java.lang.String"
                   override="false"/>
  </Context>

See
`JNDIConfiguration <https://commons.apache.org/proper/commons-configuration/apidocs/org/apache/commons/configuration2/JNDIConfiguration.html>`__
for more information on how the configuration is handled by the commons
configuration.

To load the jndi based configuration use

.. code:: java

    InitialContext initialContext = new InitialContext().lookup("path/to/my/config");
    JndiConfigurationSource jndiSource = JndiConfigurationSource.create(initialContext);

**Note**: `JNDIConfiguration <https://commons.apache.org/proper/commons-configuration/apidocs/org/apache/commons/configuration2/JNDIConfiguration.html>`__
does not support the property names with dots (``.``) for example a
key like ``beadledom.awesome.keys.key1`` in context.xml. Any
properties with dots in the name are ignored silently when copying
those properties to ImmutableCombinedConfiguration. To namespace
your properties add the property at some path. So the example
becomes ``beadledom/awesome/keys/key1``. However, the properties
are accessed using the dot notation ``config.getString("beadledom.awesome.keys.key1")``
would give the value of the key at ``beadledom/awesome/keys/key1``.

Properties Configuration
~~~~~~~~~~~~~~~~~~~~~~~~

Property file is one of the most common formats of configuration in the
Java world. A property file consists of properties with keys and values
seperated by ``=`` (with no spaces around ``=``).

.. code-block:: properties

  # config.properties

  key1=value1
  key2=value2
  key3=value3

To understand more about how the configuration handles the properties
file take a look at the documentation of `Properties
Configuration <https://commons.apache.org/proper/commons-configuration/userguide/howto_properties.html#Properties_files>`__

To load the properties-based configuration use

.. code:: java

  PropertiesConfigurationSource propertiesSource = PropertiesConfigurationSource.create(new FileReader("path/to/properties/file"));

XML Configuration
~~~~~~~~~~~~~~~~~

XML-based configurations are fairly common in the java world. Many of
the modern complex systems like hadoop, hbase loads the configuration
from xml files. Tomcat is another popular example that loads its
configurations from xml files. A typical xml based configuration would
look like

.. code:: xml

  <?xml version="1.0" encoding="UTF-8" ?>
  <arbitrary-name>
      <some-field>1</some-field>
      <another-field>2</another-field>
      <some-another-field>1</some-another-field>
  </arbitrary-name>

To load the xml based configuration use

.. code:: java

  XmlConfigurationSource xmlSource = XmlConfigurationSource.create(new FileReader("path/to/xml/based/configuration"));

Defining a custom natural ordering
----------------------------------

It is totally possible to add a custom natural ordering to the configuration sources based on some
custom criteria and still use the guice modules to load the configurations from ConfigurationSources.
All that is needed is to implement the **ConfigurationSource** interface and define the new natural
ordering in **compareTo** method. ``BeadledomConfigurationModule`` uses ``compareTo`` method to
sort the configuration sources as per the natural ordering before loading them into the ``ImmutableHierarchicalConfiguration``.

.. code:: java

  public class AwesomeConfigurationSource implements ConfigurationSource {
    @Override
    public Configuration getConfig() {
      //build a Configuration and return it here.
    }

    @Override
    public int getPriority() {
      // Priority at which this configuration needs to be loaded.
    }
    @Override
    public final int compareTo(@Nonnull ConfigurationSource that) {
      // logic for implementing the natural ordering of the configuration sources usually but not necessarily based on the priority.
    }
  }

Adding Custom ConfigurationSource
---------------------------------

Adding a custom configuration source is fairly easy process. All that is required is to implement the
**AbstractConfigurationSource** and implement the missing abstract methods. By extending this abstract
class the implementation adheres to the default natural ordering i.e., higher priority configuration
sources are loaded first.

.. code:: java

  public class AwesomeConfigurationSource extends AbstractConfigurationSource {
    @Override
    public Configuration getConfig() {
      //build a Configuration and return it here.
    }

    @Override
    public int getPriority() {
      //Priority at which this configuration needs to be loaded.
    }
  }

Usage
-----

``beadledom-configuration`` comes with a Guice module
``BeadledomConfigurationModule``, that provides the
**ImmutableHierarchicalConfiguration** object. Consumer can use this
configuration object to retrieve all the loaded configurations. This
guice module automatically gets intalled with `Beadledom
Core <../core#beadledom-core>`__.

To build multiple ConfigurationSources use
``ConfigurationSourcesModuleBuilder#addSource`` builder method as shown
in the below code example. The
``ConfigurationSourcesModuleBuilder#build`` creates a Guice Module with
all the sources and makes it available to the ``BeadledomConfiguration``
that provides the Configuration object.

.. code:: java

  public class MyModule extends AbstractModule {

    @Override
    protected void configure() {
      install(ConfigurationSourcesModuleBuilder.newBuilder()
        .addSource(propertiesSource)
        .addSource(jndiSource)
        .addSource(xmlSource)
        .build());

      install(new BeadledomConfigurationModule());
    }
  }

.. note::
  `BeadledomConfigurationModule` automatically gets installed with `BeadledomModule`. So, if
  `BeadledomModule <https://github.com/cerner/beadledom/blob/master/core/src/main/java/com/cerner/beadledom/core/BeadledomModule.java>`__ or `ResteasyModule <https://github.com/cerner/beadledom/blob/master/resteasy/src/main/java/com/cerner/beadledom/resteasy/ResteasyModule.java>`__ are installed then it is not required to install `BeadledomConfigurationModule <https://github.com/cerner/beadledom/blob/2c208f895bdad7f50fad250df235cfae683bb94c/configuration/src/main/java/com/cerner/beadledom/configuration/BeadledomConfigurationModule.java>`__ explicitly.
