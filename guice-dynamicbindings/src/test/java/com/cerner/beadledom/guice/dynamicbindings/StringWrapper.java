package com.cerner.beadledom.guice.dynamicbindings;

import javax.inject.Inject;

/**
 * A String wrapper for use in testing Guice bindings.
 *
 * @author John Leacox
 */
public class StringWrapper {
  public final String value;

  @Inject
  StringWrapper(String value) {
    this.value = value;
  }
}
