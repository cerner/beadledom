package com.cerner.beadledom.stagemonitor;

import com.cerner.beadledom.stagemonitor.request.LogJsonRequestTraceReporter;
import com.google.inject.AbstractModule;

/**
 * A Guice module that provides Stagemonitor request logging.
 *
 * <p>Stagemonitor will also need to be configured by following the
 * <a href=https://github.com/stagemonitor/stagemonitor/wiki/Installation>Stagemonitor documentation</a>
 *
 * @author Nathan Schile
 * @author John Leacox
 * @since 1.0
 */
public class StagemonitorModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(LogJsonRequestTraceReporter.class)
        .toProvider(LogJsonRequestTraceReporterProvider.class)
        .asEagerSingleton();

    bind(RequestMonitorLifecycle.class).asEagerSingleton();
  }
}
