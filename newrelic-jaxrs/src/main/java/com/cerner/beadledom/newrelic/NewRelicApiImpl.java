package com.cerner.beadledom.newrelic;

import com.newrelic.api.agent.NewRelic;

/**
 * Default implementation that delegates to the New Relic API.
 *
 * @author Nathan Schile
 * @since 2.8
 */
public class NewRelicApiImpl implements NewRelicApi {

  @Override
  public void addCustomParameter(String key, String value) {
    NewRelic.addCustomParameter(key, value);
  }
}
