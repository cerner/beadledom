package com.cerner.beadledom.health.api;

/**
 * Media types for health resources.
 */
class MediaType {

  /**
   * A {@code String} constant representing {@value #TEXT_HTML} media type with a server relative
   * quality factor of .8
   */
  final static String TEXT_HTML = "text/html; qs=0.8";

  /**
   * A {@code String} constant representing {@value #APPLICATION_JSON} media type with a server
   * relative quality factor of .9
   */
  final static String APPLICATION_JSON = "application/json; qs=0.9";
}
