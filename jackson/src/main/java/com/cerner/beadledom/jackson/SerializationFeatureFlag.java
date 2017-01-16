package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.auto.value.AutoValue;

/**
 * A helper class to enable/disable Jackson {@link SerializationFeature}'s
 * via Guice's multibinding.
 *
 * <p>Example:
 *
 * <p><pre>{@code @ProvidesIntoSet
 * SerializationFeatureFlag getSerializationFeature() {
 *   return SerializationFeatureFlag.create(SerializationFeature.FEATURE_NAME, true);
 * }
 * }</pre>
 *
 * @author Nimesh Subramanian
 */
@AutoValue
public abstract class SerializationFeatureFlag {
  public static SerializationFeatureFlag create(SerializationFeature feature, Boolean isEnabled) {
    return new AutoValue_SerializationFeatureFlag(feature, isEnabled);
  }

  public abstract SerializationFeature feature();

  public abstract Boolean isEnabled();
}
