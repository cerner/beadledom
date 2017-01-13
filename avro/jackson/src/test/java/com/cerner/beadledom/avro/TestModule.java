package com.cerner.beadledom.avro;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

/**
 * A Avro Jackson module for tests.
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
