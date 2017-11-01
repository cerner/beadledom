beadledom-jooq
==============

Overview
--------

Beadledom jOOQ is a Google Guice extension for jOOQ for managing the database context and transaction lifecycle. The simplest way to use it is through the use of an annotation and Guice AOP interceptors. This provides functionality similar to `Guice Persist <https://github.com/google/guice/tree/master/extensions/persist>`_ for jOOQ instead of JPA.

See the `jOOQ documentation <https://www.jooq.org/learn/>`_ for details on using and configuring jOOQ.

Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-jooq</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

Install the ThreadLocalJooqModule_ Guice module to enable injecting a DSLContextProvider_ instance that can be used to retrieve a jOOQ DSLContext_ for making database calls. The ThreadLocalJooqModule_ allows accessing a DSLContext_ that assumes database access happens within a single thread; this is the default request lifecycle for JAX-RS requests so this is a safe default. Usage across multiple threads is not supported at this time.

Once the Guice module is installed, any methods that will make calls to the database using the DSLContext_ must be annotated with the JooqTransactional_ annotation. This annotation will start a unit of work and wrap the method call in a transaction. Nested transactions can be done by nesting method calls annotated with JooqTransactional_.

A DataSource_ and jOOQ SQLDialect_ must be bound with Guice for the ThreadLocalJooqModule_ to use.

.. note:: The DSLContextProvider_ must be injected via Guice (rather than manually constructed) for the JooqTransactional_ annotation to work.

Making a database call:

.. code-block:: java

  public class CreateMyModelCommand {
    private final DSLContextProvider dslContextProvider;

    @Inject
    CreateMyModelCommand(DSLContextProvider dslContextProvider) {
      this.dslContextProvider = dslContextProvider;
    }

    /**
     * Inserts a new {@link MyModel} into the database.
     */
    @JooqTransactional
    public MyModelRecord execute(String name, String description) {
      return dslContextProvider.get()
          .insertInto(MY_MODEL)
          .set(MY_MODEL.NAME, name)
          .set(MY_MODEL.DESCRIPTION, description)
          .returning()
          .fetchOne();
    }
  }

Below is an example of configuring the required binding uses a C3P0 pooling datasource.

.. code-block:: java

  public class JooqConfigurationModule extends AbstractModule {
  @Override
    protected void configure() {
      install(new ThreadLocalJooqModule());
    }

    /**
     * Load data source and connection pool information from configuration
     */
    @Provides
    @Singleton
    DataSource provideDataSource(ImmutableHierarchicalConfiguration configuration) {
      try {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(configuration.getString("config.jooq.driver_class"));

        ImmutableHierarchicalConfiguration connectionConfig =
            configuration.immutableConfigurationAt("config.jooq.connection");
        dataSource.setJdbcUrl(connectionConfig.getString("url"));
        dataSource.setUser(connectionConfig.getString("username"));
        dataSource.setPassword(connectionConfig.getString("password"));

        ImmutableHierarchicalConfiguration c3p0Config =
            configuration.immutableConfigurationAt("config.jooq.c3p0");
        dataSource.setMinPoolSize(c3p0Config.getInt("min_size"));
        dataSource.setMaxPoolSize(c3p0Config.getInt("max_size"));
        dataSource.setMaxStatementsPerConnection(c3p0Config.getInt("maxStatementsPerConnection"));
        dataSource.setAcquireIncrement(c3p0Config.getInt("acquire_increment"));
        dataSource.setTestConnectionOnCheckout(c3p0Config.getBoolean("testConnectionOnCheckout"));
        dataSource.setTestConnectionOnCheckin(c3p0Config.getBoolean("testConnectionOnCheckin"));
        dataSource.setMaxIdleTimeExcessConnections(c3p0Config.getInt("maxIdleTimeExcessConnections"));

        return dataSource;
      } catch (PropertyVetoException e) {
        throw new IllegalStateException(e);
      }
    }

    @Provides
    @Singleton
    SQLDialect provideSqlDialect(ImmutableHierarchicalConfiguration configuration) {
      return SQLDialect.valueOf(configuration.getString("config.jooq.dialect"));
    }
  }

.. _DataSource: https://docs.oracle.com/javase/8/docs/api/javax/sql/DataSource.html
.. _DSLContext: https://www.jooq.org/javadoc/latest/org/jooq/DSLContext.html
.. _DSLContextProvider: https://github.com/cerner/beadledom/tree/master/jooq/src/main/java/com/cerner/beadledom/jooq/DSLContextProvider.java
.. _JooqTransactional: https://github.com/cerner/beadledom/tree/master/jooq/src/main/java/com/cerner/beadledom/jooq/JooqTransactional.java
.. _SQLDialect: https://www.jooq.org/javadoc/latest/org/jooq/SQLDialect.html
.. _ThreadLocalJooqModule: https://github.com/cerner/beadledom/tree/master/jooq/src/main/java/com/cerner/beadledom/jooq/ThreadLocalJooqModule.java
