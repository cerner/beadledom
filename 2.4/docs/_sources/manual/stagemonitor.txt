.. _beadledom-stagemonitor:

beadledom-stagemonitor
======================

Stagemonitor_ is a tool for monitoring and tracing the stacktraces for requests. Occasionally these stack
traces may become very large and inconvenient to read. This is because stagemonitor logs the fully qualified
names of all java classes in the stack. This module intercepts the stagemonitor stack traces and condenses
package names by taking just the initials of the package name.
 
Download
--------

Download using Maven
~~~~~~~~~~~~~~~~~~~~

.. code-block:: xml

  <dependencies>
      ...
      <dependency>
          <groupId>com.cerner.beadledom</groupId>
          <artifactId>beadledom-stagemonitor</artifactId>
          <version>[Insert latest version]</version>
      </dependency>
      ...
  </dependencies>

Usage
-----

StagemonitorModule_ can be used to setup Stagemonitor using the LogJsonRequestTraceReporter.
LogJsonRequestTraceReporter is a request trace reporter that reports request traces as JSON to slf4j.

Install StagemonitorModule module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

  public class SomeModule extends BaseModule {
      @Override
      protected void configure() {
          ....
          install(new StagemonitorModule());
          ....
      }
  }

Create log4j.properties
~~~~~~~~~~~~~~~~~~~~~~~

Create log4j.properties under src/main/resources.

.. code-block:: properties

  log4j.rootLogger = INFO, Console
  log4j.appender.Console=org.apache.log4j.ConsoleAppender
  log4j.appender.Console.layout=org.apache.log4j.PatternLayout
  log4j.appender.Console.layout.conversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n

  log4j.logger.request_logger=INFO, request_logger
  log4j.appender.request_logger=org.apache.log4j.RollingFileAppender
  log4j.appender.request_logger.File=${catalina.base}/logs/request.log
  log4j.appender.request_logger.MaxFileSize=10MB
  log4j.appender.request_logger.MaxBackupIndex=1
  log4j.appender.request_logger.layout=org.apache.log4j.PatternLayout
  log4j.appender.request_logger.layout.ConversionPattern=%m%n

  log4j.logger.com.cerner.beadledom.stagemonitor.request.LogJsonRequestTraceReporter=INFO, request_logger
  log4j.additivity.com.cerner.beadledom.stagemonitor.request.LogJsonRequestTraceReporter=false

  #LogRequestTraceReporter logs to the console, which we don't want since we are logging to a file above.
  log4j.logger.org.stagemonitor.requestmonitor.reporter.LogRequestTraceReporter=ERROR

Create stagemonitor.properties
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Create stagemonitor.properties under src/main/resources.

.. code-block:: properties

  stagemonitor.instanceName=your-service-name-here
  stagemonitor.applicationName=your-service-name-here
  stagemonitor.instrument.excludeContaining=CGLIB, $$, access$, EnhancerByGuice
  # Put any packages you want to include in slow request monitor below, comma separated
  stagemonitor.instrument.include=com.cerner.beadledom
  stagemonitor.profiler.logCallStacks=true
  stagemonitor.requestmonitor.minJsonExecTimeMillis=500


JSON request logger configuration

=================================================   =====================================================================   =============
Key                                                 Description                                                             Default Value
=================================================   =====================================================================   =============
stagemonitor.requestmonitor.minJsonExecTimeMillis   Only log callstacks for requests that execute longer than this value.   1000
=================================================   =====================================================================   =============



.. _Stagemonitor: https://github.com/stagemonitor/stagemonitor/wiki
.. _StagemonitorModule: https://github.com/cerner/beadledom/blob/master/stagemonitor/src/main/java/com/cerner/beadledom/stagemonitor/StagemonitorModule.java
