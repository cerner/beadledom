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

Post commit actions
~~~~~~~~~~~~~~~~~~~~

Sometimes you need to perform an action only after the current transaction successfully commits. For example,
updating or invalidating a cache.

The JooqTransactionalHooks_ class provides the :code:`whenCommitted` method for registering callback actions that should be executed after a transaction is successfully committed. Any no-arguments lambda expression or :code:`Runnable` implementation can be passed as the action.

.. code-block:: java

  hooks.whenCommitted(() -> cache.invalidate())

The action you register will be called immediately after the current transaction is successfully committed.

If you call :code:`whenCommitted` while there isn't an active transaction, the action will be executed immediately.

If the current transaction is rolled back instead of committed, then your action will be discarded and never called.

Nested Transactions
"""""""""""""""""""

Nested transactions (savepoints) are handled correctly. Actions registered via :code:`whenCommitted` in a nested transaction will only be called after the outermost transaction is committed, and will not be called if a rollback to any outer savepoint occurs during the transaction.

.. code-block:: java

  @JooqTransactional
  public void outer() {
    hooks.whenCommitted(() -> foo());
    inner();
  }

  @JooqTransactional {
  public void inner {
    hooks.WhenCommitted(() -> bar());
  }

  // foo() and then bar() will be called when the outer transaction is committed.

.. code-block:: java

  @JooqTransactional
  public void outer() {
    hooks.whenCommitted(() -> foo());
    try {
      inner();
    } catch(Exception e) {
      // Log or handle exception, but since we caught the inner exception the outer transaction will not be rolled back.
    }
  }

  @JooqTransactional {
  public void inner {
    hooks.WhenCommitted(() -> bar());
    throw new RuntimeException();  // Raising an exception will cause this inner transaction to rollback.
  }

  // foo() will be called, but not bar()

Order of Execution
""""""""""""""""""

Actions registered with :code:`whenCommitted` for a given transaction are executed in the ordered they were registered.

Exception Handling
""""""""""""""""""

If an action registered with :code:`whenCommitted` for a given transactions throws an exception, then no later registered actions in the same transaction will be called. Since the callback actions are executed *after* a successful commit, an exception in a callback will not cause the transaction to roll back.

.. _DataSource: https://docs.oracle.com/javase/8/docs/api/javax/sql/DataSource.html
.. _DSLContext: https://www.jooq.org/javadoc/latest/org/jooq/DSLContext.html
.. _DSLContextProvider: https://github.com/cerner/beadledom/tree/master/jooq/src/main/java/com/cerner/beadledom/jooq/DSLContextProvider.java
.. _JooqTransactional: https://github.com/cerner/beadledom/tree/master/jooq/src/main/java/com/cerner/beadledom/jooq/JooqTransactional.java
.. _SQLDialect: https://www.jooq.org/javadoc/latest/org/jooq/SQLDialect.html
.. _ThreadLocalJooqModule: https://github.com/cerner/beadledom/tree/master/jooq/src/main/java/com/cerner/beadledom/jooq/ThreadLocalJooqModule.java
.. _JooqTransactionalHooks:https://github.com/cerner/beadledom/tree/master/jooq/src/main/java/com/cerner/beadledom/jooq/JooqTransactionalHooqs.java
