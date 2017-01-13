package com.cerner.beadledom.client;

import com.google.inject.Module;

import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

public class FauxContextListener extends GuiceResteasyBootstrapServletContextListener {
  @Override
  protected List<? extends Module> getModules(ServletContext context) {
    return Arrays.asList(new FauxModule());
  }
}