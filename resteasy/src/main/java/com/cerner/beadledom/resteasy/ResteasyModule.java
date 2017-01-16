package com.cerner.beadledom.resteasy;

import com.cerner.beadledom.core.BeadledomModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.servlet.ServletContext;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;

/**
 * The core guice module for Beadledom on Resteasy.
 *
 * <p>This module installs {@link BeadledomModule} for pulling all of the beadledom components
 * together, as well as the Resteasy {@link RequestScopeModule}.
 *
 * <p>To use this module extend {@link ResteasyContextListener} and provide this module and any of
 * your own modules via the {@link ResteasyContextListener#getModules(ServletContext)} method.
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link GzipContentEncodingFilter}</li>
 * </ul>
 *
 * @author John Leacox
 */
public class ResteasyModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new BeadledomModule());
    install(new RequestScopeModule());
  }

  @Provides
  GzipContentEncodingFilter provideGzipContentEncodingFilter() {
    return new GzipContentEncodingFilter();
  }
}
