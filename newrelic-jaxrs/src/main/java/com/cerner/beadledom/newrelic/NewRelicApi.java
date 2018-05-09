package com.cerner.beadledom.newrelic;

/**
 * New Relic API. The API mimics the actual New Relic API, however without the static methods.
 *
 * @author Nathan Schile
 * @since 2.8
 */
public interface NewRelicApi {

  /**
   * Add a key/value pair to the current transaction.
   *
   * @param key Custom parameter key
   * @param value Custom parameter value
   */
  void addCustomParameter(String key, String value);

}
