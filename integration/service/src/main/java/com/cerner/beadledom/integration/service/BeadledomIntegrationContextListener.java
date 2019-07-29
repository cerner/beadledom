package com.cerner.beadledom.integration.service;

import com.cerner.beadledom.resteasy.ResteasyContextListener;
import com.google.common.collect.Lists;
import com.google.inject.Module;
import java.util.List;
import javax.servlet.ServletContext;

/**
 * A Resteasy Context Listener that provides the Guice modules for the service.
 *
 * @author Nick Behrens
 */
public class BeadledomIntegrationContextListener extends ResteasyContextListener {
  @Override
  protected List<? extends Module> getModules(ServletContext context) {
    return Lists.newArrayList(new BeadledomIntegrationModule(), new ResteasyBootstrapModule());
  }
}
