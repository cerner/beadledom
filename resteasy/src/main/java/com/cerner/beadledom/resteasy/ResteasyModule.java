package com.cerner.beadledom.resteasy;

import com.cerner.beadledom.core.BeadledomModule;
import com.cerner.beadledom.resteasy.exceptionmapping.FailureExceptionMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor;

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
 *     <li>{@link FailureExceptionMapper}</li>
 * </ul>
 *
 * @author John Leacox
 */
public class ResteasyModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new BeadledomModule());
    install(new RequestScopeModule());

    bind(FailureExceptionMapper.class);
  }

  @Provides
  @Singleton
  GzipContentEncodingFilter provideGzipContentEncodingFilter() {
    return new GzipContentEncodingFilter();
  }

  @Provides
  @Singleton
  GZIPEncodingInterceptor provideGzipEncodingInterceptor() {
    return new GZIPEncodingInterceptor();
  }
}
