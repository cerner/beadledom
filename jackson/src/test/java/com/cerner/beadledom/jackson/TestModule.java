package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

/**
 * A Jackson module for tests.
 *
 * @author John Leacox
 */
public class TestModule extends Module {
  @Override
  public String getModuleName() {
    return "";
  }

  @Override
  public Version version() {
    return new Version(1, 0, 0, "", "", "");
  }

  @Override
  public void setupModule(SetupContext context) {

  }
}
