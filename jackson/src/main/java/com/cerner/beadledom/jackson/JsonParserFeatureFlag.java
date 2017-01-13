package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.google.auto.value.AutoValue;

/**
 * A helper class to enable/disable Jackson {@link JsonParser.Feature}'s
 * via Guice's multibinding.
 *
 * <p>Example:
 *
 * <p><pre>{@code @ProvidesIntoSet
 * JsonParserFeatureFlag getJsonParserFeature() {
 *   return JsonParserFeatureFlag.create(JsonParser.Feature.FEATURE_NAME, true);
 * }
 * }</pre>
 *
 * @author Nimesh Subramanian
 */
@AutoValue
public abstract class JsonParserFeatureFlag {
  public static JsonParserFeatureFlag create(JsonParser.Feature feature, Boolean isEnabled) {
    return new AutoValue_JsonParserFeatureFlag(feature, isEnabled);
  }

  public abstract JsonParser.Feature feature();

  public abstract Boolean isEnabled();
}
