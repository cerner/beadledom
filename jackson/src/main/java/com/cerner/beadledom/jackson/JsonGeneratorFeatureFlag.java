package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.auto.value.AutoValue;

/**
 * A helper class to enable/disable Jackson {@link JsonGenerator.Feature}'s
 * via Guice's multibinding.
 *
 * <p>Example:
 *
 * <p><pre>{@code @ProvidesIntoSet
 * JsonGeneratorFeatureFlag getJsonGeneratorFeature() {
 *   return JsonGeneratorFeatureFlag.create(JsonGenerator.Feature.FEATURE_NAME, true);
 * }
 * }</pre>
 *
 * @author Nimesh Subramanian
 */
@AutoValue
public abstract class JsonGeneratorFeatureFlag {
  public static JsonGeneratorFeatureFlag create(JsonGenerator.Feature feature, Boolean isEnabled) {
    return new AutoValue_JsonGeneratorFeatureFlag(feature, isEnabled);
  }

  public abstract JsonGenerator.Feature feature();

  public abstract Boolean isEnabled();
}
