package com.cerner.beadledom.client.example;

import com.google.inject.Module;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;

/**
 * Faux ContextListener for the Faux services.
 *
 * @author John Leacox
 * @since 1.0
 */
public class FauxContextListener extends GuiceResteasyBootstrapServletContextListener {
  @Override
  protected List<? extends Module> getModules(ServletContext context) {
    return Arrays.asList(new FauxModule());
  }
}
