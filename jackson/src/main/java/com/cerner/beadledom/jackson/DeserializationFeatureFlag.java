package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.auto.value.AutoValue;

/**
 * A helper class to enable/disable Jackson {@link DeserializationFeature}'s
 * via Guice's multibinding.
 *
 * <p>Example:
 *
 * <p><pre>{@code @ProvidesIntoSet
 * DeserializationFeatureFlag providesDeserializationFeature() {
 *   return DeserializationFeatureFlag.create(DeserializationFeature.FEATURE_NAME, true);
 * }
 * }</pre>
 *
 * @author Nimesh Subramanian
 */
@AutoValue
public abstract class DeserializationFeatureFlag {
  public static DeserializationFeatureFlag create(
      DeserializationFeature feature, Boolean isEnabled) {
    return new AutoValue_DeserializationFeatureFlag(feature, isEnabled);
  }

  public abstract DeserializationFeature feature();

  public abstract Boolean isEnabled();
}
