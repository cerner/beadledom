package com.cerner.beadledom.jackson;

import com.fasterxml.jackson.databind.MapperFeature;
import com.google.auto.value.AutoValue;

/**
 * A helper class to enable/disable Jackson {@link MapperFeature}'s
 * via Guice's multibinding.
 *
 * <p>Example:
 *
 * <p><pre>{@code @ProvidesIntoSet
 * MapperFeatureFlag getMapperFeature() {
 *   return MapperFeatureFlag.create(MapperFeature.FEATURE_NAME, true);
 * }
 * }</pre>
 *
 * @author Nimesh Subramanian
 */
@AutoValue
public abstract class MapperFeatureFlag {
  public static MapperFeatureFlag create(MapperFeature feature, Boolean isEnabled) {
    return new AutoValue_MapperFeatureFlag(feature, isEnabled);
  }

  public abstract MapperFeature feature();

  public abstract Boolean isEnabled();
}
